package input;

/**
 * Created by germangb on 16/06/16.
 */
public interface Mouse {

    /**
     * Get currently setIndices mouse listener
     * @return mouse listener, null if none is setIndices
     */
    MouseListener getListener ();

    /**
     * Set mouse listener
     * @param listener new mouse listener or null
     */
    void setListener (MouseListener listener);

    /**
     * Check state of mouse button
     * @param button mouse button
     */
    boolean isDown (Button button);

    /**
     * Check if mouse button has just been pressed
     * @param button mouse button to test
     */
    boolean isJustDown (Button button);

    /**
     * Check if mouse button has just been released
     * @param button mouse button to test
     */
    boolean isJustUp (Button button);

    /**
     * Get cursor X
     * @return cursor X position in pixels
     */
    float getX();

    /**
     * Get cursor Y
     * @return cursor Y position in pixels
     */
    float getY();

    /**
     * Get cursor DX
     * @return cursor DX position in pixels
     */
    float getDX();

    /**
     * Get cursor Y
     * @return cursor DY position in pixels
     */
    float getDY();
}
