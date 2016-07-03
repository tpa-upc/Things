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

    RenderState boneState = new RenderState();
    Mesh boneMesh;
    ShaderProgram boneProgram;

    @Override
    public void onInit() {
        createProgram();

        state.depthTest = DepthTest.LESS;
        state.mode = PolygonMode.FILL;

        boneState.depthTest = DepthTest.DISABLED;
        boneState.depthMask = false;
        boneState.lineWidth = 3;
    }

    private void createProgram() {
        //language=GLSL
        String vert = "#version 120\n" +
                "\n" +
                "attribute vec3 a_position;\n" +
                "attribute vec3 a_normal;\n" +
                "attribute vec2 a_uv;\n" +
                "\n" +
                "varying vec2 v_uv;\n" +
                "varying vec3 v_normal;\n" +
                "\n" +
                "uniform mat4 u_mvp;\n" +
                "uniform mat4 u_mv;\n" +
                "\n" +
                "void main () {\n" +
                "    gl_Position = u_mvp * vec4(a_position, 1.);\n" +
                "    v_uv = a_uv;\n" +
                "    v_normal = normalize((u_mv * vec4(a_normal, 0.0)).xyz);\n" +
                "}";
        //language=GLSL
        String frag = "#version 120\n" +
                "\n" +
                "varying vec2 v_uv;\n" +
                "varying vec3 v_normal;\n" +
                "\n" +
                "//out vec4 frag_color;\n" +
                "\n" +
                "uniform sampler2D u_texture;\n" +
                "\n" +
                "void main () {\n" +
                "    vec3 color = texture2D(u_texture, v_uv).rgb;\n" +
                "    float dif = clamp(dot(v_normal, vec3(0, 0, 1)), 0.0, 1.0);\n" +
                "    dif = mix(0.5, 1.0, dif);\n" +
                "    gl_FragColor = vec4(color*dif, 1.);\n" +
                "}";

        program = new ShaderProgram(vert, frag, new Attribute[] {
                Attribute.POSITION,
                Attribute.UV,
                Attribute.NORMAL
        });
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
        boneProgram = new ShaderProgram(vertBone, fragBone, new Attribute[] {
                Attribute.POSITION
        });

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
        Cat.renderer.clearColor(1, 1, 1, 1);
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

        Cat.renderer.setState(boneState);
        Cat.renderer.bindProgram(boneProgram);

        for (SkeletonComponent s : skels) {
            Transform t = s.getThing().getTransform();
            vp.set(camera.viewProjection).mul(t.model);

            List<Joint> joints = s.getSkeleton().getJoints();

            //System.out.println(joints.get(2).absolute.toString());

            for (int i = 0; i < joints.size(); ++i) {
                Joint j = joints.get(i);
                //boneProgram.setUniform("u_mvp", camera.viewProjection);
                boneProgram.setUniform("u_mvp", mvp.set(vp).mul(j.absolute));
                Cat.renderer.renderMesh(boneMesh);
            }
        }

        Cat.renderer.endFrame();

        RenderStats stats = Cat.renderer.getStats();
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
