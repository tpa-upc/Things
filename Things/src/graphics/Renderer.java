package graphics;

/**
 * Created by germangb on 17/06/16.
 */
public interface Renderer {

    /** Begin rendering a frame */
    void beginFrame ();

    /** End frame rendering. */
    void endFrame ();

    /**
     * Set state
     * @param state state variables
     */
    void setState (RenderState state);

    /**
     * Get rendering stats from last frame
     * @return stats rendering statistics
     */
    RenderStats getStats ();

    /**
     * Clear buffers
     * @param bufs buffers to be cleared
     */
    void clearBuffers (BufferType... bufs);

    /**
     * Bind framebuffer
     * @param fbo framebuffer
     */
    void bindFramebuffer (Framebuffer fbo);

    /**
     * Bind texture
     * @param unit texture slot or unit
     * @param texture texture to be bound
     */
    void bindTexture (int unit, Texture texture);

    /**
     * Set shader program
     * @param program program to be used
     */
    void bindProgram (ShaderProgram program);

    /**
     * Render a mesh using current state
     * @param mesh mesh to be rendered
     */
    void renderMesh (Mesh mesh);
}
