package graphics;

import java.nio.Buffer;

/**
 * Created by germangb on 17/06/16.
 */
public class VertexBuffer {

    /** Signal renderer to keep or throw away data once it's uploaded */
    private boolean keepData = false;

    /** signal renderer */
    private boolean dirty = true;

    /** Mesh usage */
    private Usage usage;

    /** Vertex data */
    private Buffer data = null;

    public VertexBuffer (Usage usage) {
        this.usage = usage;
    }

    /**
     * Set data. Sets dirty to true
     * @param data vertex data
     */
    public void setData (Buffer data) {
        this.data = data;
        //this.dirty = true;
    }

    /**
     * Get vertex buffer data
     * @return data buffer
     */
    public Buffer getData () {
        return data;
    }

    /**
     * Check signal. Sets dirty to false
     * @return true if data is dirty
     */
    /*public boolean dirty () {
        boolean ret = dirty;
        dirty = false;
        return ret;
    }*/

    /**
     * Dirty flag
     * @return
     */
    public boolean isDirty () {
        return dirty;
    }

    /**
     * Dirty flag setter
     * @param b
     */
    public void setDirty (boolean b) {
        this.dirty = b;
    }

    /**
     * Get usage
     * @return usage
     */
    public Usage getUsage() {
        return usage;
    }

    /**
     * Set keep data flag
     * @param keepData
     */
    public void setKeepData (boolean keepData) {
        this.keepData = keepData;
    }

    /**
     * Get keep data
     * @return
     */
    public boolean isKeepData () {
        return keepData;
    }
}
