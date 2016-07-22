package utils;

import java.nio.*;

/**
 * Created by germangb on 22/07/16.
 */
public interface Buffers {

    /**
     * Allocate a byte buffer
     * @param size size of buffer in bytes
     * @return byte buffer
     */
    ByteBuffer allocate (int size);

    /**
     * Allocate data
     * @param data
     * @return
     */
    ByteBuffer allocate (byte[] data);

    /**
     * Allocate data
     * @param data
     * @return
     */
    IntBuffer allocate (int[] data);

    /**
     * Allocate data
     * @param data
     * @return
     */
    ShortBuffer allocate (short[] data);

    /**
     * Allocate data
     * @param data
     * @return
     */
    LongBuffer allocate (long[] data);

    /**
     * Allocate data
     * @param data
     * @return
     */
    FloatBuffer allocate (float[] data);

    /**
     * Allocate data
     * @param data
     * @return
     */
    DoubleBuffer allocate (double[] data);

    /**
     * Free data allocated with allocate(int)
     * @param buffer
     */
    void free (Buffer buffer);
}
