package input;

/**
 * Created by germangb on 19/06/16.
 */
public class DummyKeyboard implements Keyboard {

    private KeyboardListener listener = null;

    @Override
    public KeyboardListener getListener() {
        return listener;
    }

    @Override
    public void setListener(KeyboardListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean isDown(Key key) {
        return false;
    }

    @Override
    public boolean isJustDown(Key key) {
        return false;
    }

    @Override
    public boolean isJustUp(Key key) {
        return false;
    }
}
