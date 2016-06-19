package input;

/**
 * Created by germangb on 16/06/16.
 */
public interface Keyboard {

    /**
     * Get currently setIndices keyboard listener
     * @return keyboard listener
     */
    KeyboardListener getListener ();

    /**
     * Set keyboard listener
     * @param listener new keyboard listener (can be null)
     */
    void setListener (KeyboardListener listener);

    /**
     * Check state of keyboard key
     * @param key keyboard key
     */
    boolean isDown(Key key);

    /**
     * Check if keyboard key has just been pressed
     * @param key keyboard key to test
     */
    boolean isJustDown(Key key);

    /**
     * Check if keyboard key has just been released
     * @param key keyboard key to test
     */
    boolean isJustUp(Key key);
}
