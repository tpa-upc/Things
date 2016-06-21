package audio;

import utils.Destroyable;

import java.nio.Buffer;

/**
 * Created by germangb on 19/06/16.
 */
public class Sound implements Destroyable {

    /** Signal renderer */
    private boolean dirty = true;

    /** Signal renderer */
    private boolean destroy = false;

    /** Audio format */
    private AudioFormat format;

    /** Sampling rate */
    private int sampling;

    /** Sample data */
    private Buffer data = null;

    public Sound (AudioFormat format, int sampling) {
        this.format = format;
        this.sampling = sampling;
    }

    /**
     * Set sampling frequency
     * @param sampling smapling rate in Hz
     */
    public void setSampling (int sampling) {
        this.sampling = sampling;
    }

    /**
     * Get sampling rate in Hz
     * @return sampling rate in Hz
     */
    public int getSampling () {
        return sampling;
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
     * Get audio format
     * @return audio format
     */
    public AudioFormat getFormat () {
        return format;
    }

    /**
     * Set format
     * @param format audio format
     */
    public void setFormat (AudioFormat format) {
        this.format = format;
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

    @Override
    public void destroy () {
        destroy = true;
        data = null;
    }
}
