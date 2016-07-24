package terrain;

import cat.Cat;
import graphics.Texture;
import graphics.TextureFilter;
import graphics.TextureFormat;

import java.nio.ByteBuffer;

/**
 * Created by germangb on 09/07/16.
 */
public class TerrainField {

    /** Layer values */
    private float[] values;

    /** field's texture */
    private Texture texture = null;

    /** field size */
    private int size;

    /** length of the array */
    private int length;

    public TerrainField (int size, int length) {
        this.values = new float[length * length];
        this.size = size;
        this.length = length;
    }

    public Texture getTexture () {
        if (texture == null) {
            createTexture();
        }
        return texture;
    }

    private void createTexture() {
        texture = new Texture(length, length, TextureFormat.RED);
        texture.setMinFilter(TextureFilter.BILINEAR);
        texture.setMagFilter(TextureFilter.BILINEAR);

        int i = 0;
        int j = 0;
        ByteBuffer data = Cat.buffers.allocate(length*length);
        for (int x = 0; x < length; ++x) {
            for (int y = 0; y < length; ++y) {
                float val = values[i++] * 0.5f + 0.5f;
                int b = (int) (255 * val);

                data.put(j++, (byte) b);
            }
        }

        texture.setData(data.flip());
    }

    /**
     * Get value at integer position
     * @param x
     * @param z
     * @return
     */
    public float getValue (int x, int z) {
        x = Math.max(Math.min(x, length-1), 0);
        z = Math.max(Math.min(z, length-1), 0);

        return values[length * z + x];
    }

    /**
     * Set values
     * @return
     */
    public float[] getValues () {
        return values;
    }

    /**
     * Size X getter
     * @return
     */
    public int getSize () {
        return size;
    }

    /**
     *
     * @return
     */
    public int getLength () { return length; }

}
