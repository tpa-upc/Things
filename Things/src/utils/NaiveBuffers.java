package utils;

import java.nio.*;

/**
 * Created by germangb on 22/07/16.
 */
public class NaiveBuffers implements Buffers {

    @Override
    public ByteBuffer allocate(int size) {
        return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
    }

    @Override
    public ByteBuffer allocate(byte[] data) {
        return (ByteBuffer) allocate(data.length).put(data).flip();
    }

    @Override
    public IntBuffer allocate(int[] data) {
        return (IntBuffer) allocate(data.length << 2).asIntBuffer().put(data).flip();
    }

    @Override
    public ShortBuffer allocate(short[] data) {
        return (ShortBuffer) allocate(data.length << 1).asShortBuffer().put(data).flip();
    }

    @Override
    public LongBuffer allocate(long[] data) {
        return (LongBuffer) allocate(data.length << 3).asLongBuffer().put(data).flip();
    }

    @Override
    public FloatBuffer allocate(float[] data) {
        return (FloatBuffer) allocate(data.length << 2).asFloatBuffer().put(data).flip();
    }

    @Override
    public DoubleBuffer allocate(double[] data) {
        return (DoubleBuffer) allocate(data.length << 3).asDoubleBuffer().put(data).flip();
    }

    @Override
    public void free(Buffer buffer) {
    }
}
