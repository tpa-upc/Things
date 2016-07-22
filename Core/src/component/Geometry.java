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
            min.min(aux.set(position.get(i), position.get(i+1), position.get(i+2)));
            max.max(aux.set(position.get(i), position.get(i+1), position.get(i+2)));
        }
    }

    @Override
    public void onInit() {
        // add to the renderer
        RenderManager draw = thing.getScene()
                .getManager(RenderManager.class);
        draw.addGeometry(this);
    }

    @Override
    public void onFree() {
        // remove from the renderer
        RenderManager draw = thing.getScene()
                .getManager(RenderManager.class);
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
