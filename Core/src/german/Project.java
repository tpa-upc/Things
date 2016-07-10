package german;

import animation.Skeleton;
import assets.*;
import audio.AudioFormat;
import audio.Sound;
import cat.ApplicationListener;
import cat.Cat;
import component.*;
import fonts.BitmapFont;
import graphics.Mesh;
import graphics.Texture;
import input.Key;
import input.KeyboardListener;
import manager.RenderManager;
import scene.*;
import terrain.Terrain;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.Random;

/**
 * Created by germangb on 18/06/16.
 */
public class Project implements ApplicationListener {

    AssetManager manager;
    SceneGraph scene;

    Thing camera;

    @Override
    public void onInit() {
        System.out.println("init");

        scene = new SceneGraph();
        scene.addManager(new RenderManager());

        camera = new Thing(scene);
        camera.addComponent(new Camera());
        camera.addComponent(new AudioListener());
        scene.getRoot().addChild(camera);

        camera.getComponent(Camera.class).projection.setPerspective((float)Math.toRadians(55), 4f/3, 0.1f, 1000);
        camera.getTransform().position.set(-16, 16, -16);
        camera.getTransform().rotation.lookRotate(-1, 0, -1, 0, 1, 0);
        camera.addComponent(new Component() {

            float pitch = 0;
            float yaw = 0;

            @Override
            public void onUpdate() {
                Transform trans = getThing().getTransform();
                float v = 32;
                if (Cat.keyboard.isDown(Key.LEFT_SHIFT)) {
                    trans.position.y -= Cat.time.getDelta() * v;
                } else if (Cat.keyboard.isDown(Key.SPACE)) {
                    trans.position.y += Cat.time.getDelta() * v;
                }

                float lookX = (float) Math.sin(yaw);
                float lookZ = - (float) Math.cos(yaw);

                float b = 1;
                if (Cat.keyboard.isDown(Key.W)) {
                    trans.position.x += lookX * v * Cat.time.getDelta();
                    trans.position.z += lookZ * v * Cat.time.getDelta();
                } else if (Cat.keyboard.isDown(Key.S)) {
                    trans.position.x -= lookX * v * Cat.time.getDelta();
                    trans.position.z -= lookZ * v * Cat.time.getDelta();
                }

                if (Cat.keyboard.isDown(Key.D)) {
                    trans.position.z += lookX * v * Cat.time.getDelta();
                    trans.position.x -= lookZ * v * Cat.time.getDelta();
                } else if (Cat.keyboard.isDown(Key.A)) {
                    trans.position.z -= lookX * v * Cat.time.getDelta();
                    trans.position.x += lookZ * v * Cat.time.getDelta();
                }

                float f = 2;
                if (Cat.keyboard.isDown(Key.LEFT)) {
                    yaw -= Cat.time.getDelta() * f;
                } else if (Cat.keyboard.isDown(Key.RIGHT)) {
                    yaw += Cat.time.getDelta() * f;
                }

                if (Cat.keyboard.isDown(Key.UP)) {
                    pitch -= Cat.time.getDelta() * f;
                } else if (Cat.keyboard.isDown(Key.DOWN)) {
                    pitch += Cat.time.getDelta() * f;
                }

                trans.rotation.identity();
                trans.rotation.rotateX(pitch);
                trans.rotation.rotateY(yaw);
            }
        });

        manager = new SynchronousAssetManager();
        manager.addLoader(new TerrainAssetLoader(), Terrain.class);
        //manager.loadAsset("scene.json", Mesh.class);

        BitmapFontHints fontHints = new BitmapFontHints();
        fontHints.gzip = true;

        manager.loadAsset("font.json.gz", BitmapFont.class, fontHints);
        manager.loadAsset("pattern.png", Texture.class);
        manager.loadAsset("scene-skeleton.json", Skeleton.class);
        manager.loadAsset("terrain/test.json", Terrain.class);

        manager.finishLoading();

        Mesh mesh = manager.getAsset("scene.json", Mesh.class);
        Texture texture = manager.getAsset("pattern.png", Texture.class);
        BitmapFont font = manager.getAsset("font.json.gz", BitmapFont.class);

        Terrain terr = manager.getAsset("terrain/test.json", Terrain.class);

        for (int i = 0; i < 8; ++i) {
            for (int x = 0; x < 8; ++x) {
                Thing chunk = new Thing(scene);
                //chunk.getTransform().position.x = 128*i;
                //chunk.getTransform().position.z = 128*x;

                chunk.addComponent(new Geometry(terr.getMesh(i, x), texture));
                BoundingVolume volume = new BoundingVolume();
                chunk.addComponent(volume);
                volume.compute();
                scene.getRoot().addChild(chunk);
            }
        }
    }

    @Override
    public void onUpdate() {
        scene.update();
    }

    @Override
    public void destroy() {
        manager.destroy();
    }
}
