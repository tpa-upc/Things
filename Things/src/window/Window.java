package window;

/**
 * Created by germangb on 16/06/16.
 */
public interface Window {

    /**
     * Get window listener, null if none is setIndices
     * @return window listener
     */
    WindowListener getListener ();

    /**
     * Set new window listener
     * @param listener new window listener or null
     */
    void setListener (WindowListener listener);

    /**
     * Get information about the deffault framebuffer
     * @return default framebuffer info
     */
    WindowInfo getInfo ();

    /**
     * Resize window
     * @param width new window width
     * @param height new window height
     */
    void resize (int width, int height);

    /**
     * Get window width
     * @return window width
     */
   int getWidth ();

    /**
     * Get window height
     * @return window height
     */
   int getHeight();

    /**
     * Get window title
     * @return window title
     */
   String getTitle ();

}
