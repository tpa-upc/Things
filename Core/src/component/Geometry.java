package component;

import graphics.Mesh;
import manager.Render;

/**
 * Created by germangb on 19/06/16.
 */
public class Geometry extends Component {

    /** Mesh */
    private Mesh mesh;

    public Geometry(Mesh mesh) {
        this.mesh = mesh;
    }

    @Override
    public void onInit() {
        // add to the renderer
        Render draw = thing.getScene()
                .getManager(Render.class);
        draw.addGeometry(this);
    }

    @Override
    public void onFree() {
        // remove from the renderer
        Render draw = thing.getScene()
                .getManager(Render.class);
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
