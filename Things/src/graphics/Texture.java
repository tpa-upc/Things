package graphics;

import utils.Destroyable;

import java.nio.Buffer;

/**
 * Created by germangb on 18/06/16.
 */
public class Texture implements Destroyable {

    /** Destroy signal flag */
    private boolean destroy = false;

    /** Signal renderer to upload */
    private boolean dirty = true;

    /** Texture format */
    private TextureFormat format;

    /** Filters */
    private TextureFilter min, mag;

    /** Wrap */
    private TextureWrap s, t;

    /** Resolution */
    private int width, height;

    /** Texture data */
    private Buffer data;

    public Texture (int width, int height, TextureFormat format) {
        this.width = width;
        this.height = height;
        this.format = format;
        this.min = TextureFilter.BILINEAR;
        this.mag = TextureFilter.BILINEAR;
        this.s = TextureWrap.REPEAT;
        this.t = TextureWrap.REPEAT;
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
     * Get index data
     * @return index data
     */
    public Buffer getData() {
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

    @Override
    public void destroy () {
        destroy = true;
        data = null;
    }

    /**
     * Check destroy flag
     * @return
     */
    public boolean isDestroy() {
        return destroy;
    }

    public void setMinFilter (TextureFilter min) {
        this.min = min;
    }

    public void setMagFilter (TextureFilter mag) {
        this.mag = mag;
    }

    public void setWrapU (TextureWrap u) {
        this.s = u;
    }

    public void setWrapV (TextureWrap v) {
        this.t = v;
    }

    public void setFormat (TextureFormat format) {
        this.format = format;
    }

    /**
     * Min filter
     * @return
     */
    public TextureFilter getMin() {
        return min;
    }

    /**
     * Mag filter
     * @return
     */
    public TextureFilter getMag() {
        return mag;
    }

    /**
     * S wrap
     * @return
     */
    public TextureWrap getWrapU() {
        return s;
    }

    /**
     * Texture format
     * @return
     */
    public TextureFormat getFormat() {
        return format;
    }

    /**
     * T wrap
     * @return
     */
    public TextureWrap getWrapV() {
        return t;
    }

    /**
     * Texture width
     * @return
     */
    public int getWidth() {
        return width;
    }

    /**
     * Texture height
     * @return
     */
    public int getHeight() {
        return height;
    }
}
