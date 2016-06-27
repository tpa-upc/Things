package german;

import animation.Skeleton;
import assets.AssetListener;
import assets.AssetManager;
import assets.SynchronousAssetManager;
import cat.ApplicationListener;
import cat.Cat;
import component.*;
import fonts.BitmapFontInfo;
import graphics.Mesh;
import graphics.Texture;
import manager.RenderManager;
import scene.*;

import java.util.Random;

/**
 * Created by germangb on 18/06/16.
 */
public class Project implements ApplicationListener {

    AssetManager manager;
    SceneGraph scene;

    Thing camera;

    @Override
    public void init() {
        scene = new SceneGraph();
        scene.addManager(new RenderManager());

        camera = new Thing(scene);
        camera.addComponent(new Camera());

        camera.addComponent(new Component() {
            @Override
            public void onUpdate() {
                thing.getTransform().rotation.rotateY(Cat.mouse.getDX() * 0.01f);
            }
        });

        scene.getRoot().addChild(camera);

        camera.getComponent(Camera.class).projection.setPerspective((float)Math.toRadians(55), 4f/3, 0.1f, 100f);
        camera.getComponent(Transform.class).position.set(0, 1.5f, 0);

        manager = new SynchronousAssetManager();
        manager.setListener(new AssetListener() {
            @Override
            public void onLoaded(String file, Class<?> type) {
            }

            @Override
            public void onFailed(String file, Class<?> type, Exception e) {
                e.printStackTrace();
            }
        });

        manager.loadAsset("mesh.json", Mesh.class);
        manager.loadAsset("pattern.png", Texture.class);
        manager.loadAsset("font.json", BitmapFontInfo.class);
        manager.loadAsset("skeleton.json", Skeleton.class);
        manager.finishLoading();

        Mesh mesh = manager.getAsset("mesh.json", Mesh.class);
        Texture texture = manager.getAsset("pattern.png", Texture.class);
        BitmapFontInfo font = manager.getAsset("font.json", BitmapFontInfo.class);

        Random rand = new Random(420);
        for (int i = 0; i < 128; ++i) {
            float r = 64;
            float x = rand.nextFloat()*2-1;
            float z = rand.nextFloat()*2-1;

            Thing thing = new Thing(scene);
            thing.getTransform().position.set(x*r, 0, z*r);
            Geometry geo = new Geometry(mesh, texture);
            BoundingVolume vol = new BoundingVolume();
            thing.addComponent(geo);
            thing.addComponent(vol);
            scene.getRoot().addChild(thing);
        }

        //System.out.println(Primitive.valueOf("TRIANGLES"));
    }

    @Override
    public void update() {
        scene.update();
    }

    @Override
    public void destroy() {
        manager.destroy();
    }
}
