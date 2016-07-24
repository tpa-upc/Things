package component;

import graphics.Mesh;
import manager.RenderManager;
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
        this.terrain = terrain;
        this.x = x;
        this.z = z;
    }

    @Override
    public void onInit() {
        RenderManager draw = thing.getScene().getManager(RenderManager.class);
        draw.addTerrainChunk(this);
    }

    /**
     * Get terrain AABB
     * @return
     */
    public AABB getAABB () {
        return terrain.getVolume(x, z);
    }

    /**
     * Get terrain mesh
     * @return
     */
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
