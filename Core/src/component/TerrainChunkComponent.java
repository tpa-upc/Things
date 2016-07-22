package component;

import graphics.Mesh;
import terrain.Terrain;

/**
 * Created by germangb on 10/07/16.
 */
public class TerrainChunkComponent extends Component {

    /** terrain */
    private Terrain terrain;

    /** chunk tile */
    private int x, z;

    public TerrainChunkComponent (Terrain terrain, int x, int z) {
        super();
    }

    public Mesh getMesh () {
        return terrain.getMesh(x, z);
    }

    /**
     * Get chunk x
     * @return
     */
    public int getChunkX () {
        return x;
    }

    /**
     * Get chunk z
     * @return
     */
    public int getChunkZ () {
        return z;
    }

    /**
     * Get terrain
     * @return
     */
    public Terrain getTerrain () {
        return terrain;
    }
}
