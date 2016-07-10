package manager;

import animation.Joint;
import animation.Skeleton;
import cat.Cat;
import component.*;
import graphics.*;
import math.Matrix4f;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by germangb on 19/06/16.
 */
public class RenderManager extends Manager {

    /** Camera */
    Camera camera;

    /** Geometry to be drawn */
    List<Geometry> geometry = new ArrayList<>();

    // matrices
    Matrix4f vp = new Matrix4f();
    Matrix4f mvp = new Matrix4f();
    Matrix4f mv = new Matrix4f();

    // rendering stuff
    RenderState state = new RenderState();
    ShaderProgram program;

    RenderState finalState = new RenderState();
    ShaderProgram finalProgram;
    Mesh quadMesh;
    Framebuffer fbo;

    RenderState boneState = new RenderState();
    Mesh boneMesh;
    ShaderProgram boneProgram;

    @Override
    public void onInit() {
        createProgram();

        state.depthTest = DepthTest.LESS;
        state.mode = PolygonMode.FILL;
        state.culling = Culling.BACK;

        boneState.depthTest = DepthTest.DISABLED;
        boneState.depthMask = false;
        boneState.lineWidth = 3;
    }

    private void createProgram() {
        //language=GLSL
        String vertFinal = "#version 120\n" +
                "\n" +
                "attribute vec3 a_position;\n" +
                "\n" +
                "varying vec2 v_uv;\n" +
                "\n" +
                "void main () {\n" +
                "    gl_Position = vec4(a_position, 1.0);\n" +
                "    v_uv = a_position.xy*0.5+0.5;\n" +
                "}";
        //language=GLSL
        String fragFinal = "#version 130\n" +
                "\n" +
                "in vec2 v_uv;\n" +
                "\n" +
                "out vec4 frag_color;\n" +
                "\n" +
                "uniform sampler2D u_texture;\n" +
                "\n" +
                "void main () {\n" +
                "    vec3 color = texture2D(u_texture, v_uv).rgb;\n" +
                "    frag_color = vec4(color, 1.0);\n" +
                "}";
        finalProgram = new ShaderProgram(vertFinal, fragFinal, Attribute.POSITION);
        fbo = new Framebuffer(640/2, 480/2, true) {
            @Override
            protected void createTextures() {
                Texture tex = new Texture(width, height, TextureFormat.RGB);
                tex.setWrapU(TextureWrap.CLAMP);
                tex.setWrapV(TextureWrap.CLAMP);
                targets.add(tex);
            }
        };

        FloatBuffer quadPos = ByteBuffer.allocateDirect(100<<2).order(ByteOrder.nativeOrder()).asFloatBuffer()
                .put(new float[] {
                        -1, -1, 0,
                        +1, -1, 0,
                        -1, +1, 0,
                        +1, +1, 0
                });
        IntBuffer quadInd = ByteBuffer.allocateDirect(100<<2).order(ByteOrder.nativeOrder()).asIntBuffer()
                .put(new int[] {
                        0, 1, 2, 3
                });

        quadMesh = new Mesh(Usage.STATIC);
        quadMesh.setIndices(0, 4);
        quadMesh.setData(quadInd.flip());
        quadMesh.setPrimitive(Primitive.TRIANGLE_STRIP);

        VertexBuffer quadPosBuffer = new VertexBuffer(Usage.STATIC);
        quadPosBuffer.setData(quadPos.flip());
        quadMesh.addVertexBuffer(Attribute.POSITION, quadPosBuffer);

        //language=GLSL
        String vert = "#version 120\n" +
                "\n" +
                "attribute vec3 a_position;\n" +
                "attribute vec3 a_normal;\n" +
                "\n" +
                "varying vec2 v_uv;\n" +
                "varying vec3 v_normal;\n" +
                "varying vec3 v_position;\n" +
                "varying vec2 v_grid;\n" +
                "\n" +
                "uniform mat4 u_mvp;\n" +
                "uniform mat4 u_mv;\n" +
                "\n" +
                "void main () {\n" +
                "    gl_Position = u_mvp * vec4(a_position, 1.);\n" +
                "    v_normal = a_normal;\n" +
                "    v_position = (u_mv * vec4(a_position, 1.0)).xyz;\n" +
                "    v_grid = a_position.xz;" +
                "}";
        //language=GLSL
        String frag = "#version 130\n" +
                "\n" +
                "in vec3 v_normal;\n" +
                "in vec2 v_uv;\n" +
                "in vec3 v_position;\n" +
                "in vec2 v_grid;\n" +
                "\n" +
                "out vec4 frag_color;\n" +
                "\n" +
                "void main () {\n" +
                "    float d = 32;\n" +
                "    \n" +
                "    float fog = exp(-max(abs(v_position.z) - d, 0) * 0.01);\n" +
                "    float fog_grid = exp(-max(abs(v_position.z) - 32, 0) * 0.01);\n" +
                "    \n" +
                "    vec3 color = v_normal * 0.5 + 0.5;\n" +
                "    \n" +
                "    vec2 grid = fract(v_grid) * 2 - 1;\n" +
                "    \n" +
                "    float s = 0.5;\n" +
                "    float grid_cont = smoothstep(0, 0.1f, min(abs(grid.x - s), abs(grid.y - s)));\n" +
                "    \n" +
                "    grid_cont = mix(mix(grid_cont, 1, 0.9), 1.0, 1-fog_grid);\n" +
                "    \n" +
                "    vec3 final_color = mix(color * grid_cont, vec3(0.5), 1-fog);\n" +
                "    \n" +
                "    frag_color = vec4(final_color, 1);\n" +
                "}";

        program = new ShaderProgram(vert, frag, Attribute.POSITION,
                Attribute.NORMAL);
        program.setUniform("u_texture", 0);

        //language=GLSL
        String vertBone = "#version 120\n" +
                "\n" +
                "attribute vec3 a_position;\n" +
                "\n" +
                "uniform mat4 u_mvp;\n" +
                "\n" +
                "void main () {\n" +
                "    gl_Position = u_mvp * vec4(a_position, 1.0);\n" +
                "}";
        //language=GLSL
        String fragBone = "#version 120\n" +
                "\n" +
                "void main () {\n" +
                "    gl_FragColor = vec4(0, 0, 0, 1);\n" +
                "}";
        boneProgram = new ShaderProgram(vertBone, fragBone, Attribute.POSITION);

        // bone mesh
        boneMesh = new Mesh(Usage.STREAM);
        boneMesh.setPrimitive(Primitive.LINES);
        boneMesh.setIndices(0, 2);

        VertexBuffer attr = new VertexBuffer(Usage.STATIC);
        boneMesh.addVertexBuffer(Attribute.POSITION, attr);

        FloatBuffer data = ByteBuffer.allocateDirect(10<<2)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(new float[] {
                        0, 0.05f, 0,
                        0, 0.95f, 0
                });

        IntBuffer index = ByteBuffer.allocateDirect(10<<2)
                .order(ByteOrder.nativeOrder())
                .asIntBuffer()
                .put(new int[] {0, 1});

        attr.setData(data.flip());
        boneMesh.setData(index.flip());
    }

    @Override
    public void onUpdate() {
        Cat.renderer.beginFrame();
        Cat.renderer.bindFramebuffer(fbo);
        Cat.renderer.viewport(0, 0, fbo.getWidth(), fbo.getWidth());
        Cat.renderer.clearColor(0.5f, 0.5f, 0.5f, 1);
        Cat.renderer.clearBuffers(BufferType.COLOR, BufferType.DEPTH);
        Cat.renderer.setState(state);
        Cat.renderer.bindProgram(program);

        if (camera != null) {
            camera.updateTransforms();
            vp.set(camera.viewProjection);
        } else {
            vp.identity();
        }

        List<SkeletonComponent> skels = new ArrayList<>();

        for (Geometry geo : geometry) {
            // compute mvp
            Transform trans = geo.getThing().getComponent(Transform.class);
            BoundingVolume volume = geo.getThing().getComponent(BoundingVolume.class);
            if (volume != null && !volume.testCulling(camera)) {
                continue;
            }

            SkeletonComponent skel = geo.getThing().getComponent(SkeletonComponent.class);
            if (skel != null) {
                skels.add(skel);
            }

            mvp.set(vp).mul(trans.model);
            mv.set(camera.view).mul(trans.model);
            program.setUniform("u_mvp", mvp);
            program.setUniform("u_mv", mv);

            Mesh mesh = geo.getMesh();
            Texture tex = geo.getTexture();
            Cat.renderer.bindTexture(0, tex);
            Cat.renderer.renderMesh(mesh);
        }

        Cat.renderer.bindFramebuffer(null);
        Cat.renderer.viewport(0, 0, 640, 480);
        Cat.renderer.setState(finalState);
        Cat.renderer.clearColor(1, 0, 1, 1);
        Cat.renderer.clearBuffers(BufferType.COLOR);
        Cat.renderer.bindProgram(finalProgram);
        finalProgram.setUniform("u_texture", 0);
        Cat.renderer.bindTexture(0, fbo.getTargets().get(0));
        Cat.renderer.renderMesh(quadMesh);

        Cat.renderer.endFrame();

        //RenderStats stats = Cat.renderer.getStats();
        //System.out.println(stats.ebos+" "+Cat.time.getFps());
    }

    /**
     * Add a geometry component to the list of geometry
     * @param geo Geometry component
     */
    public void addGeometry (Geometry geo) {
        geometry.add(geo);
    }

    /**
     * Remove component from the list of geometry
     * @param geo Geometry component
     */
    public void removeGeometry (Geometry geo) {
        geometry.remove(geo);
    }

    /**
     * Set the renderer camera
     * @param camera camera used in renderer
     */
    public void setCamera (Camera camera) {
        this.camera = camera;
    }

    /**
     * Returns the camera in the renderer
     * @return camera
     */
    public Camera getCamera () {
        return camera;
    }
}
