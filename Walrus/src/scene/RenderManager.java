package scene;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by germangb on 19/06/16.
 */
public abstract class RenderManager extends Manager {

    /** Camera */
    protected Camera camera;

    /** Geometry to be drawn */
    protected List<Geometry> geometry = new ArrayList<>();

    /** Decals to be drawn */
    protected List<Decal> decals = new ArrayList<>();

    public RenderManager(Scene scene) {
        super(scene);
    }

    /**
     * Add a geometry component to the list of geometry
     * @param geo Geometry component
     */
    public void addGeometry (Geometry geo) {
        geometry.add(geo);
    }

    /**
     * Add a decal component to the list of decals
     * @param dec Decal component
     */
    public void addDecal (Decal dec) {
        decals.add(dec);
    }

    /**
     * Remove component from the list of geometry
     * @param geo Geometry component
     */
    public void removeGeometry (Geometry geo) {
        geometry.remove(geo);
    }

    /**
     * Remove component from the list of decals
     * @param dec Decal component
     */
    public void removeDecal (Decal dec) {
        decals.remove(dec);
    }

    /**
     * Set the renderer camera
     * @param camera camera used in renderer
     */
    public void setCamera (Camera camera) {
        this.camera = camera;
    }

    /**
     * Returns the camera in the renderer
     * @return camera
     */
    public Camera getCamera () {
        return camera;
    }
}
