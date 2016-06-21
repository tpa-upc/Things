package assets;

/**
 * Created by germangb on 20/06/16.
 */
public interface AssetListener {

    /**
     * Called when an asset is successfully loaded
     * @param file resource file
     * @param type resource type
     */
    void onLoaded (String file, Class<?> type);

    /**
     * Called when an asset fails loading
     * @param file asset path
     * @param type asset type
     * @param e thrown exception
     */
    void onFailed (String file, Class<?> type, Exception e);
}
