package component;

import graphics.Attribute;
import graphics.Mesh;
import graphics.Texture;
import graphics.VertexBuffer;
import manager.RenderManager;
import math.Vector3f;

import java.nio.FloatBuffer;

/**
 * Created by germangb on 19/06/16.
 */
public class Geometry extends Component {

    private static Vector3f aux = new Vector3f();

    // rendering
    private Mesh mesh;
    private Texture texture;

    // frustum culling
    private AABB volume;
    private AABB.FrustumCulling culling;

    public Geometry(Mesh mesh, Texture texture) {
        this.mesh = mesh;
        this.texture = texture;
        this.volume = new AABB();
        this.culling = AABB.FrustumCulling.DISABLED;
    }

    /** Recompute bounding volume */
    public void computeAABB () {
        volume.min.set(Float.MAX_VALUE);
        volume.max.set(Float.MIN_VALUE);

        VertexBuffer vb = mesh.getVertexBuffer(Attribute.POSITION);
        try {
            FloatBuffer data = (FloatBuffer) vb.getData();
            AABB(data, volume.min, volume.max);
        } catch (Exception ignored) {
        }

        // TODO transform AAB and recompute AAB of the transformed box

        // ...

        //max.mul(trans.scale);
        //min.mul(trans.scale);

        volume.min.add(getTransform().position);
        volume.max.add(getTransform().position);
    }

    private static void AABB (FloatBuffer position, Vector3f min, Vector3f max) {
        // compute aabb
        for (int i = 0; i < position.limit(); i += 3) {
            min.x = Math.min(min.x, position.get(i+0));
            min.y = Math.min(min.y, position.get(i+1));
            min.z = Math.min(min.z, position.get(i+2));

            max.x = Math.max(max.x, position.get(i+0));
            max.y = Math.max(max.y, position.get(i+1));
            max.z = Math.max(max.z, position.get(i+2));
        }
    }

    @Override
    public void onInit() {
        RenderManager draw = thing.getScene().getManager(RenderManager.class);
        draw.addGeometry(this);
    }

    @Override
    public void onFree() {
        RenderManager draw = thing.getScene().getManager(RenderManager.class);
        draw.removeGeometry(this);
    }

    public void setCullingTest (AABB.FrustumCulling test) {
        this.culling = test;
    }

    public AABB.FrustumCulling getCulling() {
        return culling;
    }

    public AABB getAABB() {
        return volume;
    }

    public Mesh getMesh () {
        return mesh;
    }

    public Texture getTexture () {
        return texture;
    }
}
