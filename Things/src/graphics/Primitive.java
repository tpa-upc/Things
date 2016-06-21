package graphics;

/**
 * Created by germangb on 17/06/16.
 */
public enum Primitive {

    /** Groups of three vertices */
    TRIANGLES,

    /** Group current vertex with past two */
    TRIANGLE_STRIP,

    /** Groups current vertex with first and previous */
    TRIANGLE_FAN,

    /** Group of two vertices */
    LINES,

    /** Group current vertex with previous one */
    LINE_STRIP,

    /** Same as LINE_STRIP but closes the loop */
    LINE_LOOP,

    /** Points */
    POINTS
}
