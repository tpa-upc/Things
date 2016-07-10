package assets;

import utils.Destroyable;

/**
 * Created by germangb on 20/06/16.
 */
public interface AssetManager extends Destroyable {

    /**
     * Get asset listener
     * @return asset listener
     */
    AssetListener getListener ();

    /**
     * Set asset listener
     * @param listener asset listener
     */
    void setListener (AssetListener listener);

    /**
     * Add a new asset loader for custom assets
     * @param loader asset loader
     * @param type asset type
     */
    void addLoader (AssetLoader loader, Class<?> type);

    /**
     * Load an asset from a file
     * @param file asset file path
     * @param type asset type
     */
    void loadAsset (String file, Class<?> type);

    /**
     *
     * @param file
     * @param type
     * @param hints
     */
    void loadAsset (String file, Class<?> type, Object hints);

    /**
     * Get asset
     * @param file asset file
     * @param type asset type
     * @param <T> type
     * @return asset, null if not loaded
     */
    <T> T getAsset (String file, Class<T> type);

    /**
     * Add an asset to the manager
     * @param file file (hashmap hey)
     * @param asset asset
     * @param type asset type
     * @param <T> type
     */
    <T> void addAsset (String file, T asset, Class<T> type);

    /**
     * Update asset loading
     * @return true if all assets have been loaded
     */
    boolean update ();

    /** Blocks until all assets have been loaded */
    void finishLoading ();

    @Override
    void destroy ();
}
