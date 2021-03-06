package cat;

import audio.AudioRenderer;
import files.Files;
import graphics.Renderer;
import input.Keyboard;
import input.Mouse;
import time.Time;
import utils.Buffers;
import window.Window;

/**
 * Created by germangb on 18/06/16.
 */
public class Cat {

    /** Buffer utils */
    public static Buffers buffers;

    /** Renderer */
    public static Renderer renderer;

    /** Audio renderer */
    public static AudioRenderer audio;

    /** Timing */
    public static Time time;

    /** Window */
    public static Window window;

    /** Keyboard input */
    public static Keyboard keyboard;

    /** Mouse input */
    public static Mouse mouse;

    /** File handling */
    public static Files files;

    /** Application */
    public static Application app;
}
