package files;

import java.io.InputStream;

/**
 * Created by germangb on 16/06/16.
 */
public interface Files {

    /**
     * Get an input stream for a file. Returns null if file doesn't exists
     * @param path file path
     * @return file input stream.
     */
    InputStream getFile (String path);
}
