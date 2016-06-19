package input;

/**
 * Created by germangb on 19/06/16.
 */
public class DummyMouse implements Mouse {

    private MouseListener listener = null;

    @Override
    public MouseListener getListener() {
        return listener;
    }

    @Override
    public void setListener(MouseListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean isDown(Button button) {
        return false;
    }

    @Override
    public boolean isJustDown(Button button) {
        return false;
    }

    @Override
    public boolean isJustUp(Button button) {
        return false;
    }

    @Override
    public float getX() {
        return 0;
    }

    @Override
    public float getY() {
        return 0;
    }

    @Override
    public float getDX() {
        return 0;
    }

    @Override
    public float getDY() {
        return 0;
    }
}
