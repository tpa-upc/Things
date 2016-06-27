package input;

import graphics.LwjglUtils;
import org.lwjgl.glfw.GLFWKeyCallback;
import utils.Destroyable;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by germangb on 18/06/16.
 */
public class LwjglKeyboard implements Keyboard, Destroyable {

    long window;
    KeyboardListener listener = null;
    byte[] state = new byte[GLFW_KEY_LAST+1];
    Key[] keys = new Key[GLFW_KEY_LAST+1];
    GLFWKeyCallback key;

    public LwjglKeyboard (long window) {
        this.window = window;
        update();

        for (int i = 0; i < Key.values().length; ++i) {
            keys[LwjglUtils.keyToGLFW(Key.values()[i])] = Key.values()[i];
        }

        glfwSetKeyCallback(window, key = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int i, int action, int i3) {
                if (key == GLFW_KEY_UNKNOWN) {
                    return;
                }

                if (action == GLFW_PRESS) {
                    state[key] = 1;
                    if (listener != null) listener.onKeyDown(keys[key]);
                } else if (action == GLFW_RELEASE) {
                    state[key] = -1;
                    if (listener != null) listener.onKeyUp(keys[key]);
                }
            }
        });
    }

    public void update() {
        for (int i = 0; i < state.length; ++i) {
            state[i] = 0;
        }
    }

    @Override
    public void destroy () {
        key.free();
    }

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
        int code = LwjglUtils.keyToGLFW(key);
        return glfwGetKey(window, code) == GLFW_PRESS;
    }

    @Override
    public boolean isJustDown(Key key) {
        int code = LwjglUtils.keyToGLFW(key);
        return state[code] == 1;
    }

    @Override
    public boolean isJustUp(Key key) {
        int code = LwjglUtils.keyToGLFW(key);
        return state[code] == -1;
    }
}
