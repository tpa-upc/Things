package audio;

import math.Vector3f;

/**
 * Created by germangb on 19/06/16.
 */
public interface AudioRenderer {

    public AudioStats getStats () ;

    /**
     * Set listener orientation
     * @param position listener world position
     * @param look
     * @param up up vector
     */
    void setListener(Vector3f position, Vector3f look, Vector3f up);

    /**
     * Play a sound. This method spawns a sound source, returning a handle to reference it
     * @param sound sound to be played
     * @param loop true if the sound must loop
     * @return sound handle >= 0. If < 0, the sound was unable to play
     */
    int playSound (Sound sound, boolean loop);

    /**
     * Stop a sound given its handle
     * @param handle sound handle given by the playSound() method
     */
    void stopSound (int handle);

    /**
     * Pause a sound given its handle
     * @param handle sound handle given by the playSound() method
     */
    void pauseSound (int handle);

    /**
     * Resume a sound given its handle
     * @param handle sound handle given by the playSound() method
     */
    void resumeSound (int handle);
}
