package scene;

/**
 * Created by germangb on 19/06/16.
 */
public abstract class Manager {

    /** Scene reference */
    protected Scene scene;

    public Manager(Scene scene) {
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
