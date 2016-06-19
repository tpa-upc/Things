package scene;

import java.util.*;

/**
 * Created by germangb on 19/06/16.
 */
public class Scene {

    /** Root scene */
    private Thing root = new Thing(this);

    /** Components to init */
    private Queue<Component> init = new LinkedList<>();

    /** Components to free */
    private Queue<Component> free = new LinkedList<>();

    /** Subsystems running */
    private HashMap<Class<? extends Manager>, Manager> systems = new LinkedHashMap<>();

    /** Subsystems to be initialized */
    private Queue<Manager> initSub = new LinkedList<>();

    /** Scene's renderer */
    private RenderManager renderer;

    public Scene (RenderManager renderer) {
        this.renderer = renderer;
    }

    /** Update thing */
    public void update () {
        // init components
        while (!init.isEmpty()) {
            init.poll().onInit();
        }

        // free components
        while (!free.isEmpty()) {
            free.poll().onFree();
        }

        // init subsystems
        while (!initSub.isEmpty()) {
            Manager sys = initSub.poll();
            systems.put(sys.getClass(), sys);
        }

        // update graph
        root.update();

        // update systems
        systems.values()
                .forEach(s -> s.onUpdate());
    }

    /**
     * Get scene's renderer
     * @return renderer thing
     */
    public RenderManager getRenderManager() {
        return renderer;
    }

    /**
     * Get root Thing
     * @return root thing
     */
    public Thing getRoot () {
        return root;
    }

    /**
     * Add a component to be initialized
     * @param component component
     */
    public void addInit (Component component) {
        init.add(component);
    }

    /**
     * Add a component to be freed
     * @param component component
     */
    public void addFree (Component component) {
        free.add(component);
    }
}
