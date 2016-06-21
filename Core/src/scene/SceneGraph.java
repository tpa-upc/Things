package scene;

import component.Component;
import manager.Manager;

import java.util.*;

/**
 * Created by germangb on 19/06/16.
 */
public class SceneGraph {

    /** Root scene */
    private Thing root;

    /** Components to init */
    private Queue<Component> init = new LinkedList<>();

    /** Components to free */
    private Queue<Component> free = new LinkedList<>();

    /** Subsystems running */
    private HashMap<Class<? extends Manager>, Manager> systems = new LinkedHashMap<>();

    /** Subsystems to be initialized */
    private Queue<Manager> initSub = new LinkedList<>();

    public SceneGraph() {
        this.root = new Thing(this);
    }

    /** Update thing */
    public void update () {
        // init subsystems
        while (!initSub.isEmpty()) {
            Manager sys = initSub.poll();
            sys.onInit();
            systems.put(sys.getClass(), sys);
        }

        // init component
        while (!init.isEmpty()) {
            init.poll().onInit();
        }

        // free component
        while (!free.isEmpty()) {
            free.poll().onFree();
        }

        // updateTransforms graph
        root.update();

        // updateTransforms systems
        systems.values()
                .forEach(s -> s.onUpdate());
    }

    /**
     * Add a new manager
     * @param man manager to be added
     */
    public void addManager (Manager man) {
        man.setSceneGraph(this);
        initSub.add(man);
    }

    /**
     * Get manager
     * @param type manager type
     * @param <T> type
     * @return manager
     */
    public <T extends Manager> T getManager (Class<T> type) {
        Manager man = systems.get(type);
        if (man == null) {
            return null;
        }
        return (T) man;
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
