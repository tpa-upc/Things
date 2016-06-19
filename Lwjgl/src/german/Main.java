package german;

import cat.LwjglApplication;
import cat.LwjglOptions;

/**
 * Created by germangb on 18/06/16.
 */
public class Main {

    public static void main (String[] argv) {
        LwjglOptions opts = new LwjglOptions();
        opts.width = 640;
        opts.height = 480;
        opts.resizable = false;

        LwjglApplication app = new LwjglApplication(argv, new Project(), opts);
        app.run();
    }
}
