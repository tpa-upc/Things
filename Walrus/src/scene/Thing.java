package scene;

import java.util.*;

/**
 * Created by germangb on 19/06/16.
 */
public class Thing {

    /** Scene Thing belongs to */
    private Scene scene;

    /** Thing's  parent Thing */
    private Thing parent = null;

    /** Children Things */
    private List<Thing> children = new ArrayList<>();

    /** Components ordered by priority */
    private Map<Class<? extends Component>, Component> components = new LinkedHashMap<>();

    public Thing (Scene scene) {
        this.scene = scene;
        addComponent(new Transform(this));
    }

    /** Update components */
    public void update () {
        components.values()
                .forEach(c -> c.onUpdate());

        // update children
        children.forEach(t -> t.update());
    }

    /**
     * Message all components in Thing
     * @param message message
     */
    public void message (Object message) {
        components.values()
                .forEach(c -> c.onMessage(message));
    }

    /**
     * Send a message to the whole branch
     * @param message message
     */
    public void broadcast (Object message) {
        message(message);
        children.forEach(t -> t.broadcast(message));
    }

    /**
     * Attach a child
     * @param thing child to attach
     */
    public void addChild (Thing thing) {
        thing.parent = this;
        this.children.add(thing);
    }

    /**
     * Remove a particular component
     * @param type component type
     * @param <T> type declaration
     * @return removed component
     */
    public <T extends Component> T removeComponent (Class<T> type) {
        Component comp = components.remove(type);
        if (comp == null) {
            return null;
        }
        scene.addFree(comp);
        return (T) comp;
    }

    /**
     * Get all components. Ordered by priority in hashmap.
     * Don't modify the contents of this HashMap
     * @return Hashmap with components. Highest priority first
     */
    public Map<Class<? extends Component>, Component> getComponents () {
        return components;
    }

    /**
     * Get scene
     * @return scene
     */
    public Scene getScene () {
        return scene;
    }

    /**
     * Get the transform component. All Things should have it
     * @return transform component
     */
    public Transform getTransform () {
        return getComponent(Transform.class);
    }

    /**
     * Get Parent thing
     * @return Parent Thing, null if root
     */
    public Thing getParent () {
        return parent;
    }

    /**
     * Get children attached
     * @return children
     */
    public List<Thing> getChildren () {
        return children;
    }

    /**
     * Add a component
     * @param comp component to be added
     */
    public void addComponent (Component comp) {
        scene.addInit(comp);
        components.put(comp.getClass(), comp);
    }

    /**
     * Get a component
     * @param type component type
     * @param <T> type declaration
     * @return component of the requested type. null if Thing doesn't have it
     */
    public <T extends Component> T getComponent (Class<T> type) {
        return (T) components.get(type);
    }
}
