package window;

/**
 * Created by germangb on 16/06/16.
 */
public class WindowInfo {

    /** Number of bits of the stencil buffer */
    public final int stencilBits;

    /** Number of bits of the depth buffer */
    public final int depthBits;

    /** Number of bits for the red channel */
    public final int redBits;

    /** Number of bits for the green channel */
    public final int greenBits;

    /** Number of bits for the blue channel */
    public final int blueBits;

    public WindowInfo(int stencilBits, int depthBits, int redBits, int greenBits, int blueBits) {
        this.stencilBits = stencilBits;
        this.depthBits = depthBits;
        this.redBits = redBits;
        this.greenBits = greenBits;
        this.blueBits = blueBits;
    }

    /**
     * Number of bits per pixel
     * @return bits per pixel
     */
    public int getStencilBits() {
        return stencilBits;
    }

    /**
     * Number of bits per pixel
     * @return bits per pixel
     */
    public int getDepthBits() {
        return depthBits;
    }

    /**
     * Number of bits per pixel
     * @return bits per pixel
     */
    public int getRedBits() {
        return redBits;
    }

    /**
     * Number of bits per pixel
     * @return bits per pixel
     */
    public int getGreenBits() {
        return greenBits;
    }

    /**
     * Number of bits per pixel
     * @return bits per pixel
     */
    public int getBlueBits() {
        return blueBits;
    }
}
