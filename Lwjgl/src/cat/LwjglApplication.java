package cat;

import audio.AudioRenderer;
import audio.DummyAudioRenderer;
import audio.LwjglAudioRenderer;
import files.LwjglFiles;
import graphics.LwjglRenderer;
import input.*;
import org.lwjgl.opengl.GL;
import time.LwjglTime;
import utils.Destroyable;
import window.LwjglWindow;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * Created by germangb on 18/06/16.
 */
public class LwjglApplication implements Application {

    LwjglOptions opts = new LwjglOptions();
    ApplicationListener listener;
    String[] argv;

    public LwjglApplication (String[] argv, ApplicationListener listener) {
        this.listener = listener;
        this.argv = argv;
    }

    public LwjglApplication (String[] argv, ApplicationListener listener, LwjglOptions opts) {
        this.listener = listener;
        this.opts = opts;
        this.argv = argv;
    }

    public void setOptions (LwjglOptions opts) {
        this.opts = opts;
    }

    public void run () {
        if (!glfwInit()) {
            throw new RuntimeException("Can't onInit GLFW");
        }

        // create window
        //glfwDefaultWindowHints();
        glfwWindowHint(GLFW_RESIZABLE, opts.resizable ? GLFW_TRUE : GLFW_FALSE);
        long screen = opts.fullscreen ? glfwGetPrimaryMonitor() : NULL;
        long window = glfwCreateWindow(opts.width, opts.height, opts.title, screen, NULL);
        if (window == NULL) {
            glfwTerminate();
            throw new RuntimeException("Can't create window");
        }

        glfwSwapInterval(opts.swapInterval);
        glfwMakeContextCurrent(window);

        List<Destroyable> toDestroy = new ArrayList<>();

        // Things
        LwjglRenderer renderer = new LwjglRenderer();
        LwjglWindow win = new LwjglWindow(window);
        LwjglFiles files = new LwjglFiles();
        LwjglTime time = new LwjglTime();
        Keyboard keyb = opts.hasKeyboard ? new LwjglKeyboard(window) : new DummyKeyboard();
        Mouse mouse = opts.hasMouse ? new LwjglMouse(window) : new DummyMouse();
        AudioRenderer audio = opts.hasAudio ? new LwjglAudioRenderer(opts.maxAudioSources) : new DummyAudioRenderer();

        toDestroy.add(renderer);
        toDestroy.add(win);
        if (opts.hasMouse) {
            toDestroy.add((LwjglMouse) mouse);
        }
        if (opts.hasKeyboard) {
            toDestroy.add((LwjglKeyboard) keyb);
        }
        if (opts.hasAudio) {
            toDestroy.add((LwjglAudioRenderer) audio);
        }

        Cat.time = time;
        Cat.renderer = renderer;
        Cat.app = this;
        Cat.window = win;
        Cat.keyboard = keyb;
        Cat.mouse = mouse;
        Cat.files = files;
        Cat.audio = audio;

        // main loop
        GL.createCapabilities();
        listener.onInit();

        while (!glfwWindowShouldClose(window)) {
            listener.onUpdate();
            renderer.update();
            time.update();
            if (keyb instanceof LwjglKeyboard) {
                ((LwjglKeyboard)keyb).update();
            }
            if (mouse instanceof LwjglMouse) {
                ((LwjglMouse)mouse).update();
            }
            if (audio instanceof LwjglAudioRenderer) {
                ((LwjglAudioRenderer)audio).update();
            }
            glfwPollEvents();
            glfwSwapBuffers(window);
        }

        // clean stuff
        listener.destroy();
        for (Destroyable d : toDestroy) {
            d.destroy();
        }

        glfwDestroyWindow(window);
        glfwTerminate();
    }

    @Override
    public ApplicationListener getListener() {
        return listener;
    }

    @Override
    public void setListener(ApplicationListener listener) {
        this.listener = listener;
    }

    @Override
    public String[] getArgv() {
        return argv;
    }

    @Override
    public void post(Runnable run) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void postBlocking(Runnable run) throws InterruptedException {
        throw new UnsupportedOperationException();
    }
}
