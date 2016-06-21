package german;

import assets.MeshAssetLoader;
import assets.TextureAssetLoader;
import cat.ApplicationListener;
import cat.Cat;
import component.Camera;
import component.Component;
import component.Geometry;
import component.Transform;
import graphics.Mesh;
import graphics.Texture;
import manager.RenderManager;
import scene.*;

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
        scene.addManager(new RenderManager());

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

        MeshAssetLoader loader = new MeshAssetLoader();
        Mesh mesh = null;
        try {
            mesh = loader.load("mesh.json", null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        TextureAssetLoader texLoad = new TextureAssetLoader();
        Texture texture = null;
        try {
            texture = texLoad.load("pattern.png", null);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Geometry geo = new Geometry(mesh, texture);
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
