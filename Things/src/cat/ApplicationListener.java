package cat;

/**
 * Created by germangb on 18/06/16.
 */
public interface ApplicationListener {

    /** Called at the beginning */
    void init ();

    /** Called once every frame */
    void update ();

    /** Called at the end */
    void free ();
}
