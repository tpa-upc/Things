package input;

/**
 * Created by germangb on 16/06/16.
 */
public enum Button {

    MOUSE_0 ("button_0"),
    MOUSE_1 ("button_1"),
    MOUSE_2 ("button_2"),
    MOUSE_3 ("button_3"),
    MOUSE_4 ("button_4"),
    MOUSE_5 ("button_5"),
    MOUSE_6 ("button_6"),
    MOUSE_7 ("button_7");

    /** Button name */
    public final String name;

    Button (String name) {
        this.name = name;
    }

    /**
     * Get button name
     * @return button name
     */
    public String getName () {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
