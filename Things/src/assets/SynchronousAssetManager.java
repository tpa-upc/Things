package assets;

import animation.Skeleton;
import fonts.BitmapFont;
import graphics.Mesh;
import graphics.Texture;
import utils.Destroyable;

import java.util.*;

/**
 * Created by germangb on 21/06/16.
 */
public class SynchronousAssetManager implements AssetManager {

    AssetListener listener;

    Map<Class<?>, AssetLoader> loaders = new HashMap<>();
    Queue<String> toLoad = new LinkedList<>();
    Map<String, Class<?>> types = new HashMap<>();
    Map<String, Object> loaded = new HashMap<>();
    Map<String, Object> hints = new HashMap<>();
    List<Destroyable> toDestroy = new ArrayList<>();

    public SynchronousAssetManager () {
        addLoader(new TextureAssetLoader(), Texture.class);
        addLoader(new MeshAssetLoader(), Mesh.class);
        addLoader(new BitmapFontAssetLoader(), BitmapFont.class);
        addLoader(new SkeletonAssetLoader(), Skeleton.class);
    }

    private void loadOne () {
        String file = toLoad.poll();
        Class<?> type = types.get(file);

        try {
            AssetLoader loader = loaders.get(type);
            Object asset = loader.load(file, hints.get(file));
            if (asset instanceof Destroyable) {
                toDestroy.add((Destroyable) asset);
            }
            loaded.put(file, asset);
            if (listener != null) {
                listener.onLoaded(file, type);
            }
        } catch (Exception e) {
            if (listener != null) {
                listener.onFailed(file, type, e);
            }
        }
    }

    @Override
    public AssetListener getListener() {
        return listener;
    }

    @Override
    public void setListener(AssetListener listener) {
        this.listener = listener;
    }

    @Override
    public void addLoader(AssetLoader loader, Class<?> type) {
        loaders.put(type, loader);
    }

    @Override
    public void loadAsset(String file, Class<?> type) {
        toLoad.add(file);
        types.put(file, type);
    }

    @Override
    public <T> T getAsset(String file, Class<T> type) {
        Object obj = loaded.get(file);
        if (obj == null || !(obj.getClass().equals(type))) {
            return null;
        }
        return (T) obj;
    }

    @Override
    public boolean update() {
        if (toLoad.size() > 0) {
            loadOne();
        }
        return toLoad.size() == 0;
    }

    @Override
    public void finishLoading() {
        while (toLoad.size() > 0) {
            loadOne();
        }
    }

    @Override
    public void destroy() {
        toDestroy.forEach(Destroyable::destroy);
        toDestroy.clear();
    }
}
