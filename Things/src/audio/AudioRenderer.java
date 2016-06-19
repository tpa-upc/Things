package audio;

/**
 * Created by germangb on 19/06/16.
 */
public interface AudioRenderer {

    /**
     * Play a sound.
     * @param sound sound to be played
     * @param loop true if the sound must loop
     */
    void playSound (Sound sound, boolean loop);
}
