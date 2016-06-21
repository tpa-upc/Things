package assets;

/**
 * Created by germangb on 20/06/16.
 */
public interface AssetLoader<T> {

    /**
     * Load a generic asset
     * @param path asset path
     * @param hints asset loading hints
     * @return loaded asset
     * @throws Exception in case anything bad happens
     */
    T load (String path, Object hints) throws Exception;
}
