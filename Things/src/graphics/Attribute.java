package graphics;

/**
 * Created by germangb on 17/06/16.
 */
public enum Attribute {

    POSITION ("a_position", 3, 0),
    UV ("a_uv", 2, 1),
    NORMAL ("a_normal", 3, 2),
    COLOR ("a_color", 3, 3),
    TANGENT ("a_tangent", 3, 4),
    JOINTS ("a_joints", 4, 5),
    WEIGHTS ("a_weights", 4, 6);

    /** Attribute's name */
    public final String name;

    /** Attribute size */
    public final int size;

    /** Attribute id */
    public final int id;

    Attribute (String name, int size, int id) {
        this.name = name;
        this.size = size;
        this.id = id;
    }

    /**
     * Attribute stride
     * @return stride
     */
    public int getSize() {
        return size;
    }

    /**
     * Get attribute id
     * @return attr id
     */
    public int getId() {
        return id;
    }

    /**
     * Attribute name
     * @return name
     */
    public String getName() {
        return name;
    }

    /*@Override
    public String toString() {
        return name+"("+size+")";
    }*/
}
