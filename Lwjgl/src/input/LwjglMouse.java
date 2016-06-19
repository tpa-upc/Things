package input;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by germangb on 18/06/16.
 */
public class LwjglMouse implements Mouse {

    long window;
    MouseListener listener = null;
    GLFWMouseButtonCallback call;
    GLFWCursorPosCallback pos;
    GLFWScrollCallback scr;
    byte[] state = new byte[16];
    float x, y, dx, dy;
    boolean first = true;

    public LwjglMouse (long window) {
        this.window = window;
        x = y = dx = dy = 0;
        update();

        glfwSetScrollCallback(window, scr = new GLFWScrollCallback() {
            @Override
            public void invoke(long l, double v, double v1) {
                if (listener != null) {
                    listener.onScroll((float) v, (float) v1);
                }
            }
        });

        glfwSetCursorPosCallback(window, pos = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long l, double v, double v1) {
                if (listener != null) {
                    listener.onCursorMoved((float) v, (float) v1);
                }
            }
        });

        glfwSetMouseButtonCallback(window, call = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int i2) {
                if (action == GLFW_PRESS) {
                    state[button] = 1;
                    if (listener == null) {
                        listener.onButtonDown(Button.values()[button]);
                    }
                } else if (action == GLFW_RELEASE) {
                    state[button] = -1;
                    if (listener == null) {
                        listener.onButtonUp(Button.values()[button]);
                    }
                }
            }
        });
    }

    public void update() {
        for (int i = 0; i < state.length; ++i) {
            state[i] = 0;
        }

        double[] xp = new double[1];
        double[] yp = new double[1];
        glfwGetCursorPos(window, xp, yp);

        if (first) {
            first = false;
            dx = (float) xp[0];
            dy = (float) yp[0];
        } else {
            dx = (float) xp[0] - x;
            dy = (float) yp[0] - y;
        }

        x = (float) xp[0];
        y = (float) yp[0];
    }

    public void free () {
        call.free();
        pos.free();
        scr.free();
    }

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
        return glfwGetMouseButton(window, bt2GLFW(button)) == GLFW_PRESS;
    }

    @Override
    public boolean isJustDown(Button button) {
        return state[bt2GLFW(button)] == 1;
    }

    @Override
    public boolean isJustUp(Button button) {
        return state[bt2GLFW(button)] == -1;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public float getDX() {
        return dx;
    }

    @Override
    public float getDY() {
        return dy;
    }

    private static int bt2GLFW (Button bt) {
        switch (bt) {
            case MOUSE_0: return GLFW_MOUSE_BUTTON_1;
            case MOUSE_1: return GLFW_MOUSE_BUTTON_2;
            case MOUSE_2: return GLFW_MOUSE_BUTTON_3;
            case MOUSE_3: return GLFW_MOUSE_BUTTON_4;
            case MOUSE_4: return GLFW_MOUSE_BUTTON_5;
            case MOUSE_5: return GLFW_MOUSE_BUTTON_6;
            case MOUSE_6: return GLFW_MOUSE_BUTTON_7;
            case MOUSE_7: return GLFW_MOUSE_BUTTON_8;
            default:
                throw new RuntimeException("lol");
        }
    }
}
