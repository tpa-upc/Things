package component;

import math.FrustumIntersection;
import math.Vector3f;

/**
 * Created by germangb on 19/06/16.
 */
public class AABB {

    public enum FrustumCulling {
        DISABLED, POSITIVE, NEGATIVE
    }

    /** AABB box min */
    public final Vector3f min = new Vector3f(0);

    /** AABB box max */
    public final Vector3f max = new Vector3f(0);

    /**
     * Copy aabb params
     * @param aabb
     */
    public void set (AABB aabb) {
        min.set(aabb.min);
        max.set(aabb.max);
    }

    /**
     * Test frustum culling
     * @param camera camera
     * @return true if Thing passes the test
     */
    public boolean testCulling (Camera camera) {
        FrustumIntersection culler = camera.getFrustumCuller();
        return culler.testAab(min, max);
    }
}
