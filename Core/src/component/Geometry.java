package component;

import graphics.Mesh;
import graphics.Texture;
import manager.RenderManager;

/**
 * Created by germangb on 19/06/16.
 */
public class Geometry extends Component {

    private Mesh mesh;
    private Texture texture;

    public Geometry(Mesh mesh, Texture texture) {
        this.mesh = mesh;
        this.texture = texture;
    }

    @Override
    public void onInit() {
        // add to the renderer
        RenderManager draw = thing.getScene()
                .getManager(RenderManager.class);
        draw.addGeometry(this);
    }

    @Override
    public void onFree() {
        // remove from the renderer
        RenderManager draw = thing.getScene()
                .getManager(RenderManager.class);
        draw.removeGeometry(this);
    }

    public Mesh getMesh () {
        return mesh;
    }

    public Texture getTexture () {
        return texture;
    }
}
