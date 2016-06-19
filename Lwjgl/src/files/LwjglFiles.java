package files;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by germangb on 19/06/16.
 */
public class LwjglFiles implements Files {

    @Override
    public InputStream getFile(String path) {
        try {
            return new FileInputStream("res/"+path);
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
            return null;
        }
    }
}
