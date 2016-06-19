package cat;

/**
 * Created by germangb on 18/06/16.
 */
public interface Application {

    /**
     * Get application listener
     * @return application listener
     */
    ApplicationListener getListener ();

    /**
     * Set application listener
     * @param listener app listener
     */
    void setListener (ApplicationListener listener);

    /**
     * Application arguments
     * @return arguments array
     */
    String[] getArgv ();

    /**
     * Execute a runnable. Execute asynchronously
     * @param run runnable to be ran
     */
    void post (Runnable run);

    /**
     * Execute a runnable. Block until it finishes
     * @param run runnable to be ran
     */
    void postBlocking (Runnable run) throws InterruptedException;
}
