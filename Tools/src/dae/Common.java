package dae;

/**
 * Created by germangb on 03/07/16.
 */
public class Common {

    static class JsonMeta {
        String name = "mesh_name";
        String author = "germangb";
        String tool = "collada-2-json-0";
    }

    static class JsonAttribute {
        String name;
        float[] data;
    }

    static class JsonMesh {
        JsonMeta meta;
        String primitive;
        JsonAttribute[] attributes;
        int[] data;
    }
}
