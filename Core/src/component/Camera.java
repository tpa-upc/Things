package component;

import manager.Render;
import math.FrustumIntersection;
import math.Matrix4f;
import math.Quaternionf;
import math.Vector3f;

/**
 * Created by germangb on 19/06/16.
 */
public class Camera extends Component {

    /** Utility for frustum culling computations */
    private FrustumIntersection culler = new FrustumIntersection();

    /** view matrix */
    public final Matrix4f view = new Matrix4f();

    /** inverted view matrix */
    public final Matrix4f invView = new Matrix4f();

    /** projection matrix */
    public final Matrix4f projection = new Matrix4f();

    /** inverted projection matrix */
    public final Matrix4f invProjection = new Matrix4f();

    /** viewProjection matrix */
    public final Matrix4f viewProjection = new Matrix4f();

    /** inverted viewProjection matrix */
    public final Matrix4f invViewProjection = new Matrix4f();

    private static Quaternionf aux = new Quaternionf();

    /** Update camera. Compute useful stuff */
    public void updateTransforms() {
        // compute view transform
        Transform trans = thing.getComponent(Transform.class);
        Vector3f pos = trans.position;
        view.identity()
                .rotate(aux.set(trans.rotation))
                .translate(-pos.x, -pos.y, -pos.z);

        // Compute viewprojection
        viewProjection.set(projection).mul(view);

        // invert matrices
        invView.set(view).invert();
        invProjection.set(projection).invert();
        invViewProjection.set(viewProjection).invert();

        // updateTransforms culler
        culler.set(viewProjection);
    }

    @Override
    public void onInit() {
        // set as renderer camera
        thing.getScene()
                .getManager(Render.class)
                .setCamera(this);
    }

    @Override
    public void onFree() {
        thing.getScene()
                .getManager(Render.class)
                .setCamera(null);
    }

    /**
     * Get frustum culler utility
     * @return culler utility
     */
    public FrustumIntersection getFrustumCuller() {
        return culler;
    }
}
