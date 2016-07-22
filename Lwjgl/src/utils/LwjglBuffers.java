package utils;

import org.lwjgl.system.jemalloc.JEmalloc;

import java.nio.*;

/**
 * Created by germangb on 22/07/16.
 */
public class LwjglBuffers implements Buffers {

    @Override
    public ByteBuffer allocate(int size) {
        return JEmalloc.je_malloc(size);
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
        //System.out.println("free "+buffer);
        if (buffer instanceof ByteBuffer) {
            JEmalloc.je_free((ByteBuffer) buffer);
        } else if (buffer instanceof IntBuffer) {
            JEmalloc.je_free((IntBuffer) buffer);
        } else if (buffer instanceof LongBuffer) {
            JEmalloc.je_free((LongBuffer) buffer);
        } else if (buffer instanceof ShortBuffer) {
            JEmalloc.je_free((ShortBuffer) buffer);
        } else if (buffer instanceof DoubleBuffer) {
            JEmalloc.je_free((DoubleBuffer) buffer);
        } else if (buffer instanceof FloatBuffer) {
            JEmalloc.je_free((FloatBuffer) buffer);
        } else {
            throw new IllegalArgumentException("Error...");
        }

        //System.out.println("freed "+buffer);
    }
}
