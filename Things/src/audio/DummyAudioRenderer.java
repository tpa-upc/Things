package audio;

import math.Vector3f;

/**
 * Created by germangb on 19/06/16.
 */
public class DummyAudioRenderer implements AudioRenderer {

    AudioStats stats = new AudioStats();

    @Override
    public AudioStats getStats() {
        return stats;
    }

    @Override
    public void setListener(Vector3f position, Vector3f look, Vector3f up) {
    }

    @Override
    public int playSound(Sound sound, boolean loop) {
        return -1;
    }

    @Override
    public void stopSound(int handle) {
    }

    @Override
    public void pauseSound(int handle) {

    }

    @Override
    public void resumeSound(int handle) {
    }
}
