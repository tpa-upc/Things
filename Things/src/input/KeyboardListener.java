package input;

/**
 * Created by germangb on 16/06/16.
 */
public interface KeyboardListener {

    /**
     * Called when keyboard key is pressed
     * @param key keyboard key
     */
    void onKeyDown (Key key);

    /**
     * Called when keyboard key is released
     * @param key keyboard key
     */
    void onKeyUp (Key key);
}
