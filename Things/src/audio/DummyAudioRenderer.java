package audio;

import math.Vector3f;

/**
 * Created by germangb on 19/06/16.
 */
public class DummyAudioRenderer implements AudioRenderer {

    @Override
    public void setListener(Vector3f position, Vector3f look, Vector3f up) {
    }

    @Override
    public void playSound(Sound sound, boolean loop) {
    }
}
