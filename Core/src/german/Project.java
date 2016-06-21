package german;

import assets.AssetManager;
import assets.SynchronousAssetManager;
import cat.ApplicationListener;
import cat.Cat;
import component.Camera;
import component.Component;
import component.Geometry;
import component.Transform;
import fonts.BitmapFontInfo;
import graphics.Mesh;
import graphics.Texture;
import manager.RenderManager;
import math.Quaternionf;
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
            Quaternionf rot = new Quaternionf();
            @Override
            public void onUpdate() {
                //rot.rotateX(0.2275f * Cat.time.getDelta());
                //rot.rotateY(0.4376f * Cat.time.getDelta());
                //rot.rotateZ(0.2561f * Cat.time.getDelta());
                rot.rotateY(Cat.mouse.getDX() * 0.02431f);
                rot.rotateX(Cat.mouse.getDY() * 0.02431f);

                thing.getTransform().rotation.slerp(rot, Cat.time.getTime()*0.005f);
            }
        });

        camera = new Thing(scene);
        camera.addComponent(new Camera());
        scene.getRoot().addChild(camera);

        camera.getComponent(Camera.class).projection.setPerspective((float)Math.toRadians(55), 4f/3, 0.1f, 100f);
        camera.getComponent(Transform.class).position.set(-1, 2, 2);
        camera.getComponent(Transform.class).rotation.lookRotate(-1, 2, 2, 0, 1, 0);

        AssetManager manager = new SynchronousAssetManager();
        manager.loadAsset("mesh.json", Mesh.class);
        manager.loadAsset("pattern.png", Texture.class);
        manager.loadAsset("font.json", BitmapFontInfo.class);
        manager.finishLoading();

        Mesh mesh = manager.getAsset("mesh.json", Mesh.class);
        Texture texture = manager.getAsset("pattern.png", Texture.class);
        BitmapFontInfo font = manager.getAsset("font.json", BitmapFontInfo.class);

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
