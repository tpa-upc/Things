package cat;

import utils.Destroyable;

/**
 * Created by germangb on 18/06/16.
 */
public interface ApplicationListener extends Destroyable {

    /** Called at the beginning */
    void onInit();

    /** Called once every frame */
    void onUpdate();

    @Override
    void destroy ();
}
