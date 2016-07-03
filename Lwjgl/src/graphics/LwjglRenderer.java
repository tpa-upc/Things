package graphics;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import utils.Destroyable;
import utils.LwjglUtils;

import java.nio.*;
import java.util.*;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * Created by germangb on 17/06/16.
 */
public class LwjglRenderer implements Renderer, Destroyable {

    Map<ShaderProgram, LwjglUtils.GLSLProgram> programs;
    Map<Mesh, Integer> indices;
    Map<VertexBuffer, Integer> buffers;
    Map<Texture, Integer> textures;
    Map<Framebuffer, Integer> framebuffers;

    ShaderProgram usedProgram = null;
    RenderState defaultState = new RenderState();
    RenderStats stats = new RenderStats();
    RenderStats statsPrevious = new RenderStats();

    long lastCheck = System.currentTimeMillis();

    public LwjglRenderer () {
        this.programs = new HashMap<>();
        this.buffers = new HashMap<>();
        this.indices = new HashMap<>();
        this.textures = new HashMap<>();
        this.framebuffers = new HashMap<>();
    }

    public void update () {
        checkDestroy();
    }

    private void checkDestroy () {
        long now = System.nanoTime();
        if (now - lastCheck > 3000) {
            List<Mesh> destroyMesh = new ArrayList<>();
            List<VertexBuffer> destroyVbo = new ArrayList<>();
            List<Texture> destroyTex = new ArrayList<>();
            List<ShaderProgram> destroyProg= new ArrayList<>();

            for (Iterator<ShaderProgram> it = programs.keySet().iterator(); it.hasNext();) {
                ShaderProgram prog = it.next();
                if (prog.isDestroy()) {
                    destroyProg.add(prog);
                }
            }

            for (Iterator<Mesh> it = indices.keySet().iterator(); it.hasNext();) {
                Mesh mesh = it.next();
                if (mesh.isDestroy()) {
                    destroyMesh.add(mesh);

                    Map<Attribute, VertexBuffer> buffers = mesh.getVertexBuffers();
                    destroyVbo.addAll(buffers.values());
                }
            }

            for (Iterator<Texture> it = textures.keySet().iterator(); it.hasNext();) {
                Texture tex = it.next();
                if (tex.isDestroy()) {
                    destroyTex.add(tex);
                }
            }

            for (int i = 0; i < destroyProg.size(); ++i) {
                LwjglUtils.GLSLProgram id = programs.remove(destroyProg.get(i));
                glDeleteShader(id.vertex);
                glDeleteShader(id.fragment);
                glDeleteProgram(id.program);
            }

            for (int i = 0; i < destroyMesh.size(); ++i) {
                int id = indices.remove(destroyMesh.get(i));
                glDeleteBuffers(id);
            }

            for (int i = 0; i < destroyVbo.size(); ++i) {
                int id = buffers.remove(destroyVbo.get(i));
                glDeleteBuffers(id);
            }

            for (int i = 0; i < destroyTex.size(); ++i) {
                int id = textures.remove(destroyTex.get(i));
                glDeleteTextures(id);
            }
        }
    }

    @Override
    public void destroy () {
        indices.values().forEach(GL15::glDeleteBuffers);
        buffers.values().forEach(GL15::glDeleteBuffers);
        textures.values().forEach(GL11::glDeleteTextures);
        programs.values().forEach(p -> {
            glDeleteShader(p.vertex);
            glDeleteShader(p.fragment);
            glDeleteProgram(p.program);
        });
    }

    @Override
    public void beginFrame() {
        glGetError();

        glPushAttrib(GL_ALL_ATTRIB_BITS);
        setState(defaultState);

        // clear stats
        stats.ebos = 0;
        stats.vbos = 0;
        stats.fbosSwitches = 0;
        stats.textureSwitches = 0;
        stats.programSwitches = 0;
        stats.vertices = 0;

        // push state
    }

    private static void checkError () {
        int error = glGetError();
        if (error != GL_NO_ERROR) {
            throw new RuntimeException("GL Error "+error);
        }
    }

    @Override
    public void endFrame() {
        usedProgram = null;

        glPopAttrib();

        // set stats
        statsPrevious.ebos = stats.ebos;
        statsPrevious.vbos = stats.vbos;
        statsPrevious.fbosSwitches = stats.fbosSwitches;
        statsPrevious.textureSwitches = stats.textureSwitches;
        statsPrevious.programSwitches = stats.programSwitches;
        statsPrevious.vertices = stats.vertices;

        checkError();
    }

    @Override
    public void setState(RenderState state) {
        glColorMask(state.redMask, state.greenMask, state.blueMask, state.alphaMask);
        glDepthMask(state.depthMask);
        glPolygonMode(GL_FRONT_AND_BACK, LwjglUtils.renderMode(state.mode));
        glPointSize(state.pointSize);
        glLineWidth(state.lineWidth);
        LwjglUtils.setCulling(state.culling);
        LwjglUtils.setDepthTest(state.depthTest);
        LwjglUtils.setBlending(state.blending);
    }

    @Override
    public RenderStats getStats() {
        // stats from last frame
        return statsPrevious;
    }

    @Override
    public void clearColor(float r, float g, float b, float a) {
        glClearColor(r, g, b, a);
    }

    @Override
    public void clearBuffers(BufferType... bufs) {
        glClear(LwjglUtils.getBufferBitmask(bufs));
    }

    @Override
    public void bindFramebuffer(Framebuffer fbo) {

        if (fbo == null) {
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
            return;
        }

        Integer id = framebuffers.get(fbo);
        boolean bound = false;
        if (id == null) {
            id = glGenFramebuffers();
            framebuffers.put(fbo, id);

            bound = true;
            glBindFramebuffer(GL_FRAMEBUFFER, id);
            stats.fbosSwitches++;

            // create textures
            List<Texture> tex = fbo.getTargets();
            int i = 0;
            for (Texture t : tex) {
                bindTexture(0, t);
                int tid = textures.get(t);
                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0+i, GL_TEXTURE_2D, tid, 0);
                i++;
            }

            // depth texture
            Texture depth = fbo.getDepth();
            if (depth != null) {
                bindTexture(0, depth);
                int tid = textures.get(depth);
                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, tid, 0);
            }

            // only depth fbo
            if (tex.size() == 0 && depth != null) {
                glDrawBuffer(GL_NONE);
                glReadBuffer(GL_NONE);
            }

            // fbo status
            int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
            if (status != GL_FRAMEBUFFER_COMPLETE) {
                throw new RuntimeException("FBO not complete ("+status+")");
            }
        }

        if (!bound) {
            glBindFramebuffer(GL_FRAMEBUFFER, id);
            stats.fbosSwitches++;
        }

        List<Texture> tex = fbo.getTargets();
        IntBuffer bufs = ByteBuffer.allocateDirect(tex.size()<<2)
                .order(ByteOrder.nativeOrder())
                .asIntBuffer();

        for (int i = 0; i < tex.size(); ++i) {
            bufs.put(GL_COLOR_ATTACHMENT0+i);
        }

        bufs.flip();
        glDrawBuffers(bufs);
    }

    @Override
    public void bindTexture(int unit, Texture texture) {
        if (texture.isDestroy()) {
            return;
        }

        Integer id = textures.get(texture);
        boolean bound = false;
        if (id == null) {
            id = glGenTextures();
            textures.put(texture, id);
            bound = true;
            texture.dirty();

            // setIndices params
            glActiveTexture(GL_TEXTURE0+unit);
            glBindTexture(GL_TEXTURE_2D, id);
            stats.textureSwitches++;
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, LwjglUtils.textureFilter(texture.getMag()));
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, LwjglUtils.textureFilter(texture.getMin()));
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, LwjglUtils.textureWrap(texture.getWrapU()));
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, LwjglUtils.textureWrap(texture.getWrapV()));

            // upload data
            int w = texture.getWidth();
            int h = texture.getHeight();
            int form = LwjglUtils.textureFormat(texture.getFormat());
            int type = GL_UNSIGNED_BYTE;
            Buffer data = texture.getData();
            glTexImage2D(GL_TEXTURE_2D, 0, form, w, h, 0, form, type, (ByteBuffer) data);
        }

        if (!bound) {
            glActiveTexture(GL_TEXTURE0+unit);
            glBindTexture(GL_TEXTURE_2D, id);
            stats.textureSwitches++;
        }

        if (texture.dirty()) {
            // update params
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, LwjglUtils.textureFilter(texture.getMag()));
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, LwjglUtils.textureFilter(texture.getMin()));
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, LwjglUtils.textureWrap(texture.getWrapU()));
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, LwjglUtils.textureWrap(texture.getWrapV()));

            // upload data
            int w = texture.getWidth();
            int h = texture.getHeight();
            int form = LwjglUtils.textureFormat(texture.getFormat());
            int type = GL_UNSIGNED_BYTE;
            Buffer data = texture.getData();
            glTexImage2D(GL_TEXTURE_2D, 0, form, w, h, 0, form, type, (ByteBuffer) data);
        }
    }

    @Override
    public void bindProgram(ShaderProgram program) {
        if (program.isDestroy()) {
            return;
        }

        // get program and compile it if it's new
        LwjglUtils.GLSLProgram pro = programs.get(program);
        if (pro == null) {
            pro = LwjglUtils.createProgram(program);
            programs.put(program, pro);
        }

        // bind the new program
        glUseProgram(pro.program);
        stats.programSwitches++;
        usedProgram = program;
    }

    private void uniforms () {
        if (usedProgram.isDestroy()) {
            return;
        }

        Map<String, Object> unifs = usedProgram.getUniforms();
        for (Iterator<String> it = unifs.keySet().iterator(); it.hasNext(); ) {
            String key = it.next();
            Object val = unifs.get(key);

            int pro = programs.get(usedProgram).program;
            int loc = glGetUniformLocation(pro, key);
            LwjglUtils.uniform(loc, val);
        }
    }

    @Override
    public void renderMesh(Mesh mesh) {
        if (usedProgram == null) {
            return;
        }

        if (usedProgram.isDestroy()) {
            return;
        }

        if (mesh.isDestroy()) {
            return;
        }

        // update vertex attributes
        Attribute[] attr = usedProgram.getAttributes();

        for (int i = 0; i < attr.length; ++i) {
            // get vertex buffer
            VertexBuffer vb = mesh.getVertexBuffer(attr[i]);
            Buffer data = vb.getData();

            Integer id = buffers.get(vb);
            boolean bound = false;
            if (id == null) {
                buffers.put(vb, id = glGenBuffers());
                bound = true;
                vb.dirty();

                glBindBuffer(GL_ARRAY_BUFFER, id);
                stats.vbos++;
                LwjglUtils.bufferData(GL_ARRAY_BUFFER, data, vb.getUsage());
            }

            if (!bound) {
                glBindBuffer(GL_ARRAY_BUFFER, id);
                stats.vbos++;
            }

            if (vb.dirty()) {
                LwjglUtils.bufferSubData(GL_ARRAY_BUFFER, data);
            }

            // enable attr and pointer
            int type = LwjglUtils.nioToSigned(data);
            glEnableVertexAttribArray(attr[i].id);
            glVertexAttribPointer(attr[i].id, attr[i].size, type, false, 0, 0);
        }

        // element buffer
        Buffer data = mesh.getData();

        Integer id = indices.get(mesh);
        boolean bound = false;
        if (id == null) {
            id = glGenBuffers();
            indices.put(mesh, id);
            bound = true;

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
            stats.ebos++;
            LwjglUtils.bufferData(GL_ELEMENT_ARRAY_BUFFER, data, mesh.getUsage());
        }

        if (!bound) {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
            stats.ebos++;
        }

        if (mesh.dirty()) {
            LwjglUtils.bufferSubData(GL_ELEMENT_ARRAY_BUFFER, data);
        }

        // uniform variables
        uniforms();

        int off = mesh.getOffset();
        if (data instanceof IntBuffer) off <<= 2;
        else if (data instanceof ShortBuffer) off <<= 1;

        int type = LwjglUtils.nioToUnsigned(data);
        int prim = LwjglUtils.primitive(mesh.getPrimitive());
        glDrawElements(prim, mesh.getCount(), type, off);
        stats.vertices += mesh.getCount();
    }
}
