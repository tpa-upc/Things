package manager;

import cat.Cat;
import component.*;
import graphics.*;
import input.Key;
import math.Matrix4f;
import math.Vector3f;
import terrain.Terrain;
import terrain.TerrainField;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by germangb on 19/06/16.
 */
public class RenderManager extends Manager {

    final static int SCALE = 2;

    /** Camera */
    Camera camera;

    /** Terrain to be drawn */
    List<TerrainChunkComponent> terrain = new ArrayList<>();
    List<Geometry> geometry = new ArrayList<>();

    // matrices
    Matrix4f vp = new Matrix4f();
    Matrix4f mvp = new Matrix4f();
    Matrix4f mv = new Matrix4f();

    // rendering stuff
    RenderState state = new RenderState();
    ShaderProgram program;

    // AABB rendering
    Mesh aabbMesh;
    ShaderProgram aabbProgram;
    RenderState aabbState = new RenderState();
    LinkedList<AABB> aabb = new LinkedList<>();

    RenderState finalState = new RenderState();
    ShaderProgram finalProgram;
    Mesh quadMesh;
    Framebuffer fbo;

    @Override
    public void onInit() {
        createProgram();
        createAABB();

        state.depthTest = DepthTest.LESS;
        state.mode = PolygonMode.FILL;
        state.culling = Culling.BACK;

        aabbState.lineWidth = 1;
        aabbState.depthTest = DepthTest.LESS;
    }

    private void createAABB() {
        //language=GLSL
        String vert = "#version 130\n" +
                "\n" +
                "in vec3 a_position;\n" +
                "\n" +
                "uniform mat4 u_mvp;\n" +
                "\n" +
                "void main () {\n" +
                "    gl_Position = u_mvp * vec4(a_position, 1.0);\n" +
                "}";
        //language=GLSL
        String frag = "#version 130\n" +
                "\n" +
                "out vec4 frag_color;\n" +
                "\n" +
                "void main () {\n" +
                "    frag_color = vec4(1, 0, 0, 1);\n" +
                "}";
        aabbProgram = new ShaderProgram(vert, frag, Attribute.POSITION);

        aabbMesh = new Mesh(Usage.STATIC);
        VertexBuffer pos = new VertexBuffer(Usage.STATIC);
        pos.setData(Cat.buffers.allocate(new float[] {
                +0, +0, +0,
                +1, +0, +0,
                +1, +0, +1,
                +0, +0, +1,

                +0, +1, +0,
                +1, +1, +0,
                +1, +1, +1,
                +0, +1, +1
        }));

        aabbMesh.setData(Cat.buffers.allocate(new int[] {
                0, 1,
                1, 2,
                2, 3,
                3, 0,

                4, 5,
                5, 6,
                6, 7,
                7, 4,

                0, 4,
                1, 5,
                2, 6,
                3, 7
        }));

        aabbMesh.setIndices(0, 24);
        aabbMesh.setPrimitive(Primitive.LINES);
        aabbMesh.addVertexBuffer(Attribute.POSITION, pos);
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
        fbo = new Framebuffer(Cat.window.getWidth()/SCALE, Cat.window.getHeight()/SCALE, true) {
            @Override
            protected void createTextures() {
                Texture tex = new Texture(width, height, TextureFormat.RGB);
                tex.setWrapU(TextureWrap.CLAMP);
                tex.setWrapV(TextureWrap.CLAMP);
                targets.add(tex);
            }
        };

        FloatBuffer quadPos = Cat.buffers.allocate(new float[] {
                -1, -1, 0,
                +1, -1, 0,
                -1, +1, 0,
                +1, +1, 0});

        IntBuffer quadInd = Cat.buffers.allocate(new int[] {0, 1, 2, 3});

        quadMesh = new Mesh(Usage.STATIC);
        quadMesh.setIndices(0, 4);
        quadMesh.setData(quadInd);
        quadMesh.setPrimitive(Primitive.TRIANGLE_STRIP);

        VertexBuffer quadPosBuffer = new VertexBuffer(Usage.STATIC);
        quadPosBuffer.setData(quadPos);
        quadMesh.addVertexBuffer(Attribute.POSITION, quadPosBuffer);

        //language=GLSL
        String vert = "#version 120\n" +
                "\n" +
                "attribute vec3 a_position;\n" +
                "attribute vec3 a_normal;\n" +
                "attribute vec2 a_uv;\n" +
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
                "    v_grid = a_position.xz;\n" +
                "    v_uv = a_uv;\n" +
                "}";
        //language=GLSL
        String frag = "#version 130\n" +
                "\n" +
                "in vec3 v_normal;\n" +
                "in vec2 v_uv;\n" +
                "in vec3 v_position;\n" +
                "in vec2 v_grid;\n" +
                "\n" +
                "uniform sampler2D u_texture;\n" +
                "\n" +
                "out vec4 frag_color;\n" +
                "\n" +
                "void main () {\n" +
                "    float d = 0;\n" +
                "    \n" +
                "    float fog = exp(-max(abs(v_position.z) - d, 0) * 0.0025);\n" +
                "    \n" +
                "    vec3 color = v_normal * 0.5 + 0.5;\n" +
                "    \n" +
                "    vec3 field = texture2D(u_texture, v_uv).rgb;\n" +
                "    color = mix(color, vec3(1,0,1), smoothstep(0.45, 0.5, field.r));\n" +
                "    \n" +
                "    vec3 final_color = mix(color, vec3(0.5), 1-fog);\n" +
                "    \n" +
                "    frag_color = vec4(final_color, 1);\n" +
                "}";

        program = new ShaderProgram(vert, frag, Attribute.POSITION, Attribute.NORMAL, Attribute.UV);
        program.setUniform("u_texture", 0);
    }

    @Override
    public void onUpdate() {
        Cat.renderer.beginFrame();
        Cat.renderer.bindFramebuffer(fbo);
        Cat.renderer.viewport(0, 0, fbo.getWidth(), fbo.getWidth());
        Cat.renderer.clearColor(0.5f, 0.5f, 0.5f, 1);
        Cat.renderer.clearBuffers(BufferType.COLOR, BufferType.DEPTH);

        state.mode = Cat.keyboard.isDown(Key.L) ? PolygonMode.WIREFRAME: PolygonMode.FILL;

        // render terrain
        Cat.renderer.setState(state);
        Cat.renderer.bindProgram(program);

        if (camera != null) {
            camera.updateTransforms();
            vp.set(camera.viewProjection);
        } else {
            vp.identity();
        }

        // render terrain chunks
        boolean texSet = false;
        for (TerrainChunkComponent chunk : terrain) {
            if (!texSet) {
                texSet = true;
                Terrain terr = chunk.getTerrain();
                Map<String, TerrainField> fields = terr.getFields();

                for (TerrainField field : fields.values()) {
                    Texture tex = field.getTexture();
                    Cat.renderer.bindTexture(0, tex);
                    program.setUniform("u_texture", 0);
                }
            }

            Transform trans = chunk.getTransform();
            AABB volume = chunk.getAABB();

            if (!volume.testCulling(camera)) {
                continue;
            }

            // compute mvp
            mvp.set(vp).mul(trans.model);
            mv.set(camera.view).mul(trans.model);
            program.setUniform("u_mvp", mvp);
            program.setUniform("u_mv", mv);

            Mesh mesh = chunk.getMesh();
            Cat.renderer.renderMesh(mesh);
            aabb.add(volume);
        }

        Cat.renderer.bindProgram(aabbProgram);
        Cat.renderer.setState(aabbState);

        Matrix4f trans = new Matrix4f();
        Vector3f sca = new Vector3f();

        aabb.clear();
        while (!aabb.isEmpty()) {
            AABB v = aabb.poll();
            trans.identity();
            trans.translate(v.min);
            trans.scale(sca.set(v.max).sub(v.min));

            aabbProgram.setUniform("u_mvp", mvp.set(camera.viewProjection).mul(trans));
            Cat.renderer.renderMesh(aabbMesh);
        }

        Cat.renderer.bindFramebuffer(null);
        Cat.renderer.viewport(0, 0, Cat.window.getWidth(), Cat.window.getHeight());
        Cat.renderer.setState(finalState);
        Cat.renderer.clearColor(1, 0, 1, 1);
        Cat.renderer.clearBuffers(BufferType.COLOR);
        Cat.renderer.bindProgram(finalProgram);
        finalProgram.setUniform("u_texture", 0);
        Cat.renderer.bindTexture(0, fbo.getTargets().get(0));
        Cat.renderer.renderMesh(quadMesh);

        Cat.renderer.endFrame();

        if (Cat.time.getFrame() % 60 == 0) {
            RenderStats stats = Cat.renderer.getStats();
            System.out.println("EBO=" + stats.ebos + "\tFPS=" + Cat.time.getFps());
        }
    }

    public void addGeometry (Geometry geo) {
        geometry.add(geo);
    }

    public void removeGeometry (Geometry geo) {
        geometry.remove(geo);
    }

    public void addTerrainChunk (TerrainChunkComponent chunk) { terrain.add(chunk); }

    public void removeTerrainChunk (TerrainChunkComponent chunk) { terrain.remove(chunk); }

    public void setCamera (Camera camera) {
        this.camera = camera;
    }

    public Camera getCamera () {
        return camera;
    }
}
