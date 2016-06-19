package graphics;

/**
 * Created by germangb on 18/06/16.
 */
public enum DepthTest {

    /** Disable depth test */
    DISABLED,

    /** Never passes */
    NEVER,

    /** Always passes */
    ALWAYS,

    /** Passes if less (<) (default) */
    LESS,

    /** Passes if less or equals (<=) */
    LEQUAL,

    /** Passes if greater (>) */
    GREATER,

    /** Passes if greater or equals (>=) */
    GEQUAL,

    /** Passes if equals */
    EQUAL,

    /** Not equals */
    NOTEQUAL
}
