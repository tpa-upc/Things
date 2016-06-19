package scene;

import graphics.Mesh;

/**
 * Created by germangb on 19/06/16.
 */
public class Geometry extends Component {

    /** Mesh */
    private Mesh mesh;

    public Geometry(Thing thing, Mesh mesh) {
        super(thing);
        this.mesh = mesh;
    }

    @Override
    public void onInit() {
        // add to the renderer
        RenderManager draw = thing.getScene().getRenderManager();
        draw.addGeometry(this);
    }

    @Override
    public void onFree() {
        // remove from the renderer
        RenderManager draw = thing.getScene().getRenderManager();
        draw.removeGeometry(this);
    }

    /**
     * Get mesh
     * @return mesh
     */
    public Mesh getMesh () {
        return mesh;
    }
}
