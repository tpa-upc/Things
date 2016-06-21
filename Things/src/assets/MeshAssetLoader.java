package assets;

import cat.Cat;
import com.google.gson.Gson;
import graphics.*;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by germangb on 21/06/16.
 */
public class MeshAssetLoader implements AssetLoader<Mesh> {

    static class JsonMetadata {
        String name;
        String author;
    }

    static class JsonAttribute {
        String name;
        float[] data;
    }

    static class JsonMesh {
        JsonMetadata metadata;
        String primitive;
        JsonAttribute[] attributes;
        int[] data;
    }

    @Override
    public Mesh load(String path, Object hints) throws Exception {
        Gson gson = new Gson();
        Reader reader = new InputStreamReader(Cat.files.getFile(path));
        JsonMesh parsed = gson.fromJson(reader, JsonMesh.class);

        // create mesh
        Mesh mesh = new Mesh(Usage.STATIC);

        Primitive prim = null;

        switch (parsed.primitive) {
            case "TRIANGLES": prim = Primitive.TRIANGLES; break;
            case "TRIANGLE_STRIP": prim = Primitive.TRIANGLE_STRIP; break;
            case "TRIANGLE_FAN": prim = Primitive.TRIANGLE_FAN; break;
            case "LINES": prim = Primitive.LINES; break;
            case "LINE_STRIP": prim = Primitive.LINE_STRIP; break;
            case "LINE_LOOP": prim = Primitive.LINE_LOOP; break;
            case "POINTS": prim = Primitive.POINTS; break;
            default:
                // :(
                throw new RuntimeException("Wront primitive ("+parsed.primitive+")");
        }

        // attributes
        for (int i = 0; i < parsed.attributes.length; ++i) {
            JsonAttribute attr = parsed.attributes[i];
            Attribute attribute = null;

            switch (attr.name) {
                case "POSITION": attribute = Attribute.POSITION; break;
                case "UV": attribute = Attribute.UV; break;
                case "NORMAL": attribute = Attribute.NORMAL; break;
                case "COLOR": attribute = Attribute.COLOR; break;
                case "TANGENT": attribute = Attribute.TANGENT; break;
                case "JOINT": attribute = Attribute.JOINT; break;
                case "WEIGHT": attribute = Attribute.WEIGHT; break;
                default:
                    throw new RuntimeException("Unknown attribute ("+attr.name+")");
            }

            VertexBuffer pos = new VertexBuffer(attribute, Usage.STATIC);
            FloatBuffer posData = ByteBuffer.allocateDirect(attr.data.length<<2).order(ByteOrder.nativeOrder())
                    .asFloatBuffer()
                    .put(attr.data);
            pos.setData(posData.flip());
            mesh.addVertexBuffer(pos);
        }

        mesh.setIndices(0, parsed.data.length);

        return mesh;
    }
}
