package scene;

/**
 * Created by germangb on 19/06/16.
 */
public class Decal extends Component {

    public Decal(Thing thing) {
        super(thing);
    }

    @Override
    public void onInit() {
        // add to the renderer
        RenderManager draw = thing.getScene().getRenderManager();
        draw.addDecal(this);
    }

    @Override
    public void onFree() {
        // remove from the renderer
        RenderManager draw = thing.getScene().getRenderManager();
        draw.removeDecal(this);
    }
}
