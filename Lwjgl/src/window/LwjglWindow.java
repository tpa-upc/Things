package window;

import org.lwjgl.glfw.GLFWWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by germangb on 17/06/16.
 */
public class LwjglWindow implements Window {

    // glfw win params
    private long window;
    private int width, height;

    // callbacks
    private GLFWWindowSizeCallback sizeCall;

    // listener
    private WindowListener listener = null;

    public LwjglWindow (long window) {
        this.window = window;

        // window size callback
        glfwSetWindowSizeCallback(window, sizeCall = new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int w, int h) {
                width = w;
                height = h;
                if (listener != null) {
                    listener.onResize(width, height);
                }
            }
        });
    }

    public void free () {
        sizeCall.free();
    }

    @Override
    public WindowListener getListener() {
        return listener;
    }

    @Override
    public void setListener(WindowListener listener) {
        this.listener = listener;
    }

    @Override
    public WindowInfo getInfo() {
        WindowInfo info = new WindowInfo(8, 8, 8, 8, 8);
        return info;
    }

    @Override
    public void resize(int width, int height) {
        glfwSetWindowSize(width, width, height);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public String getTitle() {
        return "GLFW window";
    }
}
