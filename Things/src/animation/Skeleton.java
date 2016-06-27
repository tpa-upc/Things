package animation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by germangb on 27/06/16.
 */
public class Skeleton {

    /** List of joints */
    private List<Joint> joints = new ArrayList<>();

    /** Hashmap of joints */
    private Map<String, Joint> hash = new HashMap<>();

    public Skeleton () {
    }

    /**
     * Get a joint by name
     * @param name joint name
     * @return
     */
    public Joint getJoint (String name) {
        return hash.get(name);
    }

    /**
     * Add a joint to the skeleton
     * @param joint joint to be added
     */
    public void addJoint (Joint joint) {
        joint.skeleton = this;
        joints.add(joint);
        hash.put(joint.getName(), joint);
    }

    /** Update joint transformations */
    public void update () {
        joints.forEach(Joint::update);
    }

    /**
     * Get all joints (Don't modify the contents of this list)
     * @return list of joints
     */
    public List<Joint> getJoints () {
        return joints;
    }
}
