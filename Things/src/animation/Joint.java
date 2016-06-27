package animation;

import math.Matrix4f;
import math.Quaternionf;
import math.Vector3f;

/**
 * Created by germangb on 27/06/16.
 */
public class Joint {

    /** Skeleton */
    protected Skeleton skeleton;

    /** Parent joint */
    private Joint parent;

    /** Joint name */
    private String name;

    /** Translation relative to parent */
    public final Vector3f translate = new Vector3f();

    /** Rotation relative to parent */
    public final Quaternionf rotation = new Quaternionf();

    /** Scale relative to parent */
    public final Vector3f scale = new Vector3f(1f);

    /** Local transformation */
    public final Matrix4f local = new Matrix4f();

    /** Absolute transformation (parent.absolute * local) */
    public final Matrix4f absolute = new Matrix4f();

    public Joint (Joint parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    /** Update transformations */
    public void update () {
        // local transformation
        local.identity()
                .translate(translate)
                .rotate(rotation)
                .scale(scale);

        if (parent != null) {
            // absolute transformation
            absolute.set(parent.absolute)
                    .mul(local);
        } else {
            absolute.set(local);
        }
    }

    /**
     * Get parent joint
     * @return
     */
    public Joint getParent() {
        return parent;
    }

    /**
     * Parent setter
     * @param parent
     */
    public void setParent(Joint parent) {
        this.parent = parent;
    }

    /**
     * Name getter
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Name setter
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }
}
