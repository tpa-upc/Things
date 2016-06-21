package assets;

import fonts.BitmapFontInfo;
import graphics.Mesh;
import graphics.Texture;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

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

    public SynchronousAssetManager () {
        addLoader(new TextureAssetLoader(), Texture.class);
        addLoader(new MeshAssetLoader(), Mesh.class);
        addLoader(new BitmapFontInfoAssetLoader(), BitmapFontInfo.class);
    }

    private void loadOne () {
        String file = toLoad.poll();
        Class<?> type = types.get(file);

        try {
            AssetLoader loader = loaders.get(type);
            Object asset = loader.load(file, hints.get(file));
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
        while (!update());
    }

    @Override
    public void free() {

    }
}
