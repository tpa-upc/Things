package graphics;

import java.nio.Buffer;
import java.util.*;

/**
 * Created by germangb on 17/06/16.
 */
public class Mesh {

    /** signal destruction to the renderer */
    private boolean destroy = false;

    /** Signal renderer to upload */
    private boolean dirty = true;

    /** Vertex indices */
    private Buffer data = null;

    /** Index offset */
    private int offset = 0;

    /** Index count */
    private int count = 0;

    /** Mesh usage to hint the renderer */
    private Usage usage;

    /** vertex buffers */
    private Map<Attribute, VertexBuffer> buffers;

    /** Mesh primitive */
    private Primitive primitive = Primitive.TRIANGLES;

    public Mesh (Usage usage) {
        this.usage = usage;
        this.buffers = new HashMap<>();
    }

    /**
     * Add vertex buffer
     * @param buff vertex buffer
     */
    public void addVertexBuffer (VertexBuffer buff) {
        this.buffers.put(buff.getAttribute(), buff);
    }

    /**
     * Get vertex buffers
     * @return vertex buffers
     */
    public Iterator<VertexBuffer> getBuffers () {
        return buffers.values().iterator();
    }

    /**
     * Get a specific vertex buffer by attribute
     * @param attr vertex attribute
     * @return vertex buffer or null
     */
    public VertexBuffer getBuffer (Attribute attr) {
        return buffers.get(attr);
    }

    /**
     * Set data. Sets dirty to true
     * @param data vertex data
     */
    public void setData (Buffer data) {
        this.data = data;
        this.dirty = true;
    }

    /**
     * Index count
     * @return
     */
    public int getCount() {
        return count;
    }

    /**
     * Get offset
     * @return
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Set offset and count
     * @param offset index offset
     * @param count index count
     */
    public void setIndices(int offset, int count) {
        this.offset = offset;
        this.count = count;
    }

    /**
     * Get index data
     * @return index data
     */
    public Buffer getData() {
        return data;
    }

    /**
     * Get usage
     * @return
     */
    public Usage getUsage() {
        return usage;
    }

    /**
     * Get render primitive
     * @return
     */
    public Primitive getPrimitive() {
        return primitive;
    }

    /**
     * Check signal. Sets dirty to false
     * @return true if data is dirty
     */
    public boolean dirty () {
        boolean ret = dirty;
        dirty = false;
        return ret;
    }

    /** Destroy mesh assets. CPU & GPU*/
    public void destroy () {
        destroy = true;
        data = null;

        // delete vbos
        buffers.values().forEach(vbo -> {
            vbo.setData(null);
        });
    }

    /**
     * Return true if mesh has to be destroyed
     * @return
     */
    public boolean isDestroy () {
        return destroy;
    }
}
