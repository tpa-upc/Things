package german;

import cat.ApplicationListener;
import cat.Cat;
import component.Camera;
import component.Component;
import component.Geometry;
import component.Transform;
import graphics.Attribute;
import graphics.Mesh;
import graphics.Usage;
import graphics.VertexBuffer;
import manager.Render;
import scene.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by germangb on 18/06/16.
 */
public class Project implements ApplicationListener {

    SceneGraph scene;

    Thing thing;
    Thing camera;

    @Override
    public void init() {
        scene = new SceneGraph();
        scene.addManager(new Render());

        thing = new Thing(scene);
        thing.addComponent(new Component() {
            @Override
            public void onUpdate() {
                thing.getTransform().rotation.rotateX(0.2275f * Cat.time.getDelta());
                thing.getTransform().rotation.rotateY(0.4376f * Cat.time.getDelta());
                thing.getTransform().rotation.rotateZ(0.2561f * Cat.time.getDelta());
                thing.getTransform().rotation.rotateY(Cat.mouse.getDX() * 0.02431f);
                thing.getTransform().rotation.rotateX(Cat.mouse.getDY() * 0.02431f);
            }
        });

        camera = new Thing(scene);
        camera.addComponent(new Camera());
        scene.getRoot().addChild(camera);

        camera.getComponent(Camera.class).projection.setPerspective((float)Math.toRadians(55), 4f/3, 0.1f, 100f);
        camera.getComponent(Transform.class).position.set(-2, 4, 4);
        camera.getComponent(Transform.class).rotation.lookRotate(-1, 2, 2, 0, 1, 0);

        Mesh mesh = new Mesh(Usage.STATIC);

        VertexBuffer pos = new VertexBuffer(Attribute.POSITION, Usage.STATIC);
        pos.setData(ByteBuffer.allocateDirect(90<<2)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(new float[] {
                        -1, -1, 1,
                        1, -1, 1,
                        0, 1, 0,

                        -1, -1, -1,
                        1, -1, -1,
                        0, 1, 0,

                        -1, -1, -1,
                        -1, -1, 1,
                        0, 1, 0,

                        1, -1, -1,
                        1, -1, 1,
                        0, 1, 0,
                }).flip());

        mesh.addVertexBuffer(pos);

        mesh.setData(ByteBuffer.allocateDirect(12<<2)
                .order(ByteOrder.nativeOrder())
                .asIntBuffer()
                .put(new int[] {
                        0, 1, 2,
                        3, 4, 5,
                        6, 7, 8,
                        9, 10, 11
                }).flip());

        mesh.setIndices(0, 12);

        Geometry geo = new Geometry(mesh);
        thing.addComponent(geo);

        scene.getRoot().addChild(thing);
    }

    @Override
    public void update() {
        scene.update();
    }

    @Override
    public void free() {

    }
}
