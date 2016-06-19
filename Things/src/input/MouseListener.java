package input;

/**
 * Created by germangb on 16/06/16.
 */
public interface MouseListener {

    /**
     * Called when mouse cursor is moved
     * @param x new cursor position
     * @param y new cursor position
     */
    void onCursorMoved (float x, float y);

    /**
     *
     * @param x
     * @param y
     */
    void onScroll (float x, float y);

    /**
     * Called when mouse button is pressed
     * @param button mouse button
     */
    void onButtonDown (Button button);

    /**
     * Called when mouse button is released
     * @param button mouse button
     */
    void onButtonUp (Button button);
}
