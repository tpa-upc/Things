package german;

import cat.ApplicationListener;
import cat.Cat;
import graphics.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

/**
 * Created by germangb on 18/06/16.
 */
public class Project implements ApplicationListener {

    Mesh mesh;
    ShaderProgram program;
    Texture texture;
    Framebuffer fbo;

    @Override
    public void init() {
        fbo = new Framebuffer(640, 480, true);

        createProgram();
        createMesh();
        createTexture();
        program.setUniform("u_texture", 0);
        mesh.setIndices(0, 3);
    }

    private void createTexture () {
        texture = new Texture(128, 128, TextureFormat.RED);
        byte[] pixels = new byte[128*128];
        for (int i = 0; i < pixels.length; ++i) {
            int r = i/128;
            int c = i%128;
            pixels[i] = (byte)(r^c);
        }
        texture.setData(ByteBuffer.allocateDirect(128*128)
                .order(ByteOrder.nativeOrder())
                .put(pixels)
                .flip());
    }

    private void createMesh () {
        mesh = new Mesh(Usage.STATIC, Primitive.TRIANGLES);
        VertexBuffer pos = new VertexBuffer(Attribute.POSITION, Usage.STREAM);
        pos.setData(ByteBuffer.allocateDirect(9<<2)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(new float[] {
                        -1, -1, 0,
                        1, 0, 0,
                        0, 1, 0
                })
                .flip());

        VertexBuffer col = new VertexBuffer(Attribute.COLOR, Usage.STATIC);
        col.setData(ByteBuffer.allocateDirect(9<<2)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(new float[] {
                        1, 0, 0,
                        0, 1, 0,
                        0, 0, 1
                })
                .flip());

        VertexBuffer tex = new VertexBuffer(Attribute.UV, Usage.STATIC);
        tex.setData(ByteBuffer.allocateDirect(6<<2)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(new float[] {
                        1, 0,
                        1, 1,
                        0, 1
                })
                .flip());

        mesh.addVertexBuffer(pos);
        mesh.addVertexBuffer(col);
        mesh.addVertexBuffer(tex);

        mesh.setData(ByteBuffer.allocateDirect(3<<2)
                .order(ByteOrder.nativeOrder())
                .asIntBuffer()
                .put(new int[] {0, 1, 2})
                .flip());
    }

    private void createProgram () {
        //language=GLSL
        String vert = "#version 130\n" +
                "\n" +
                "in vec3 a_position;\n" +
                "in vec3 a_color;\n" +
                "in vec2 a_uv;\n" +
                "\n" +
                "out vec3 v_color;\n" +
                "out vec2 v_uv;\n" +
                "\n" +
                "void main () {\n" +
                "    gl_Position = vec4(a_position, 1.0);\n" +
                "    v_color = a_color;\n" +
                "    v_uv = a_uv;\n" +
                "}";

        //language=GLSL
        String frag = "#version 130\n" +
                "\n" +
                "in vec3 v_color;\n" +
                "in vec2 v_uv;\n" +
                "\n" +
                "out vec4 frag_color;\n" +
                "\n" +
                "uniform sampler2D u_texture;\n" +
                "\n" +
                "void main () {\n" +
                "    float r = texture2D(u_texture, v_uv).r;\n" +
                "    frag_color = vec4(v_color*r, 1.0);\n" +
                "}";

        program = new ShaderProgram(vert, frag, new Attribute[] { Attribute.POSITION, Attribute.COLOR, Attribute.UV });
    }

    Random rand = new Random();

    @Override
    public void update() {
        float r0 = rand.nextFloat()*0.1f+0.9f;
        float r1 = rand.nextFloat()*0.1f+0.9f;
        float r2 = rand.nextFloat()*0.1f+0.9f;
        float r3 = rand.nextFloat()*0.1f+0.9f;

        if (!mesh.isDestroy()) {
            VertexBuffer pos = mesh.getBuffer(Attribute.POSITION);
            ((FloatBuffer) pos.getData()).put(new float[]{
                    -1 * r0, -1 * r1, 0,
                    1 * r2, 0, 0,
                    0, 1 * r3, 0
            }).flip();
            pos.setData(pos.getData());
        }

        Cat.renderer.beginFrame();
        Cat.renderer.bindFramebuffer(fbo);
        Cat.renderer.clearBuffers(BufferType.COLOR);
        Cat.renderer.bindFramebuffer(null);

        RenderState st = new RenderState();
        st.depthTest = DepthTest.DISABLED;
        Cat.renderer.setState(st);

        Cat.renderer.clearBuffers(BufferType.COLOR);
        Cat.renderer.bindTexture(0, texture);
        Cat.renderer.bindProgram(program);
        Cat.renderer.renderMesh(mesh);
        Cat.renderer.endFrame();
    }

    @Override
    public void free() {

    }
}
