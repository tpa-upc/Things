package time;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by germangb on 20/06/16.
 */
public class LwjglTime implements Time {

    int frames = 0;
    float lastFrame = 0;
    float lastFpsTime = 0;
    int countFrames = 0;
    int lastFps = 0;
    float delta = 0;

    public void update () {
        ++frames;
        float now = (float) glfwGetTime();
        delta = now - lastFrame;
        lastFrame = now;
        if (now - lastFpsTime > 1) {
            lastFps = countFrames;
            countFrames = 0;
            lastFpsTime = now;
        } else {
            countFrames++;
        }
    }

    @Override
    public float getTime() {
        return (float) glfwGetTime();
    }

    @Override
    public float getDelta() {
        return delta;
    }

    @Override
    public float getFps() {
        return lastFps;
    }

    @Override
    public int getFrame() {
        return frames;
    }
}
