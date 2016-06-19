package input;

/**
 * Created by germangb on 16/06/16.
 */
public enum Key {

    NUM_0("0"),
    NUM_1("1"),
    NUM_2("2"),
    NUM_3("3"),
    NUM_4("4"),
    NUM_5("5"),
    NUM_6("6"),
    NUM_7("7"),
    NUM_8("8"),
    NUM_9("9"),

    A("a"),
    B("b"),
    C("c"),
    D("d"),
    E("e"),
    F("f"),
    G("g"),
    H("h"),
    I("i"),
    J("j"),
    K("k"),
    L("l"),
    M("m"),
    N("n"),
    O("o"),
    P("p"),
    Q("q"),
    R("r"),
    S("s"),
    T("t"),
    U("u"),
    V("v"),
    W("w"),
    X("x"),
    Y("y"),
    Z("z"),

    LEFT_BRACKET("left_bracket"),
    BACKSLASH("backslash"),
    RIGHT_BRACKET("right_bracket"),
    GRAVE_ACCENT("grave_accent"),
    ESCAPE("escape"),
    ENTER("enter"),
    TAB("tab"),
    SPACE("space"),
    BACKSPACE("backspace"),
    INSERT("insert"),
    DELETE("delete"),
    RIGHT("right"),
    LEFT("left"),
    DOWN("down"),
    UP("up"),
    PAGE_UP("page_up"),
    PAGE_DOWN("page_down"),
    HOME("home"),
    END("end"),
    CAPS_LOCK("caps_lock"),
    SCROLL_LOCK("scroll_lock"),
    NUM_LOCK("num_lock"),
    PRINT_SCREEN("print_screen"),
    PAUSE("pause"),

    F1("f1"),
    F2("f2"),
    F3("f3"),
    F4("f4"),
    F5("f5"),
    F6("f6"),
    F7("f7"),
    F8("f8"),
    F9("f9"),
    F10("f10"),
    F11("f12"),
    F12("f12"),
    F13("f13"),
    F14("f14"),
    F15("f15"),
    F16("f16"),
    F17("f17"),
    F18("f18"),
    F19("f19"),
    F20("f20"),
    F21("f21"),
    F22("f22"),
    F23("f23"),
    F24("f24"),
    F25("f25"),

    LEFT_SHIFT("left_shift"),
    LEFT_CTRL("left_control"),
    LEFT_ALT("left_alt"),
    LEFT_SUPER("left_super"),
    RIGHT_SHIFT("right_shift"),
    RIGHT_CTRL("right_control"),
    RIGHT_ALT("right_alt"),
    RIGHT_SUPER("right_super"),
    MENU("menu");

    /** Button name */
    public final String name;

    Key (String name) {
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
