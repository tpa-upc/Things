package manager;

import scene.SceneGraph;

/**
 * Created by germangb on 19/06/16.
 */
public abstract class Manager {

    /** SceneGraph reference */
    protected SceneGraph scene;

    /**
     * Set scene graph
     * @param scene scene graph ref
     */
    public void setSceneGraph (SceneGraph scene) {
        this.scene = scene;
    }

    /** Called at the beginning to initialize */
    public void onInit () {
    }

    /** Called once every frame */
    public void onUpdate () {
    }

    /** Called at the end. */
    public void onFree () {
    }
}
