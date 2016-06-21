package component;

import graphics.Attribute;
import graphics.VertexBuffer;
import math.FrustumIntersection;
import math.Vector3f;

import java.nio.FloatBuffer;

/**
 * Created by germangb on 19/06/16.
 */
public class Boundary extends Component {

    private static Vector3f aux = new Vector3f();

    /** AABB box min */
    public final Vector3f min = new Vector3f(1);

    /** AABB box max */
    public final Vector3f max = new Vector3f(1);

    /** frustum culling enabling flag */
    private boolean ignoreCulling = false;

    /**
     * Recompute AABB.
     * Only works if there is a Geometry Component attache to the Thing.
     */
    public void compute () {
        Geometry geo = thing.getComponent(Geometry.class);
        Transform trans = thing.getComponent(Transform.class);

        min.set(Float.MAX_VALUE);
        max.set(Float.MIN_VALUE);

        // test geometry
        if (geo != null) {
            VertexBuffer vb = geo.getMesh().getBuffer(Attribute.POSITION);
            try {
                FloatBuffer data = (FloatBuffer) vb.getData();
                AABB(data, min, max);
            } catch (Exception e) {
            }
        }

        min.add(trans.position);
        max.add(trans.position);
    }

    /**
     * Test frustum culling
     * @param camera camera
     * @return true if Thing passes the test
     */
    public boolean testCulling (Camera camera) {
        if (ignoreCulling) {
            // always pass the test
            return true;
        }

        // test culling
        FrustumIntersection culler = camera.getFrustumCuller();
        return culler.testAab(min, max);
    }

    private static void AABB (FloatBuffer position, Vector3f min, Vector3f max) {
        // compute aabb
        for (int i = 0; i < position.limit(); i += 3) {
            min.min(aux.set(position.get(i), position.get(i+1), position.get(i+2)));
            max.max(aux.set(position.get(i), position.get(i+1), position.get(i+2)));
        }
    }


    /**
     * Disable/enable frustum culling test
     * @param ignore true to ignore culling
     */
    public void setIgnoreCulling (boolean ignore) {
        ignoreCulling = ignore;
    }
}
