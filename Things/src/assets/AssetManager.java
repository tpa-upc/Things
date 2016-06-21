package assets;

/**
 * Created by germangb on 20/06/16.
 */
public interface AssetManager {

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
     * Get asset
     * @param file asset file
     * @param type asset type
     * @param <T> type
     * @return asset, null if not loaded
     */
    <T> T getAsset (String file, Class<T> type);

    /**
     * Update asset loading
     * @return true if all assets have been loaded
     */
    boolean update ();

    /** Blocks until all assets have been loaded */
    void finishLoading ();
}
