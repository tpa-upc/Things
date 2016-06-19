package graphics;

/**
 * Created by germangb on 18/06/16.
 */
public class RenderState {

    // culling
    public Culling culling = Culling.DISABLED;

    // blending
    public Blending blending = Blending.DISABLED;

    // mode
    public PolygonMode mode = PolygonMode.FILL;

    // color stuff
    public boolean redMask = true;
    public boolean greenMask = true;
    public boolean blueMask = true;
    public boolean alphaMask = true;

    // depth stuff
    public boolean depthMask = true;
    public DepthTest depthTest = DepthTest.DISABLED;
}
