package terrain;

/**
 * Created by germangb on 09/07/16.
 */
public class TerrainField {

    /** Layer values */
    private float[] values = null;

    /** field size */
    private int size;

    public TerrainField (int size) {
        this.values = new float[size*size];
        this.size = size;
    }

    /**
     * Get value at integer position
     * @param x
     * @param z
     * @return
     */
    public float getValue (int x, int z) {
        x = Math.max(Math.min(x, size-1), 0);
        z = Math.max(Math.min(z, size-1), 0);

        return values[size * z + x];
    }

    /**
     * Get interpolated value.
     * @param x
     * @param z
     * @return
     */
    public float getValue (float x, float z) {
        return getValue((int) x, (int) z);
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

}
