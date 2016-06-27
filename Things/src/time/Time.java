package time;

/**
 * Created by germangb on 20/06/16.
 */
public interface Time {

    /**
     * Get current time in seconds
     * @return current time in seconds
     */
    float getTime ();

    /**
     * Get frame time
     * @return frame time in seconds
     */
    float getDelta ();

    /**
     * Get frames per second in frames
     * @return frames per second in frames
     */
    float getFps ();

    /**
     * Get the current frame
     * @return current frame
     */
    int getFrame ();
}
