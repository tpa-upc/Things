package scene;

import component.Component;
import component.Transform;

import java.util.*;

/**
 * Created by germangb on 19/06/16.
 */
public class Thing {

    /** SceneGraph Thing belongs to */
    private SceneGraph scene;

    /** Thing's  parent Thing */
    private Thing parent = null;

    /** Thing's transform */
    private Transform transform;

    /** Children Things */
    private List<Thing> children = new ArrayList<>();

    /** Components ordered by priority */
    private Map<Class<? extends Component>, Component> components = new LinkedHashMap<>();

    public Thing (SceneGraph scene) {
        this.scene = scene;
        addComponent(transform = new Transform());
    }

    /** Update component */
    public void update () {
        components.values()
                .forEach(c -> c.onUpdate());

        // updateTransforms children
        children.forEach(t -> t.update());
    }

    /**
     * Message all component in Thing
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
     * Get transform component
     * @return transform component
     */
    public Transform getTransform () {
        return transform;
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
     * Get all component. Ordered by priority in hashmap.
     * Don't modify the contents of this HashMap
     * @return Hashmap with component. Highest priority first
     */
    public Map<Class<? extends Component>, Component> getComponents () {
        return components;
    }

    /**
     * Get scene
     * @return scene
     */
    public SceneGraph getScene () {
        return scene;
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
        comp.setThing(this);
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
