package assets;

import cat.Cat;
import com.google.gson.Gson;
import graphics.*;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

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

        try {
            prim = Primitive.valueOf(parsed.primitive);
        } catch (Exception e) {
            throw new RuntimeException("Wront primitive ("+parsed.primitive+")");
        } finally {
            mesh.setPrimitive(prim);
        }

        // attributes
        for (int i = 0; i < parsed.attributes.length; ++i) {
            JsonAttribute attr = parsed.attributes[i];
            Attribute attribute;

            try {
                attribute = Attribute.valueOf(attr.name);
            } catch (Exception e) {
                throw new RuntimeException("Unknown attribute ("+attr.name+")");
            }

            VertexBuffer pos = new VertexBuffer(attribute, Usage.STATIC);
            FloatBuffer posData = ByteBuffer.allocateDirect(attr.data.length<<2).order(ByteOrder.nativeOrder())
                    .asFloatBuffer()
                    .put(attr.data);
            pos.setData(posData.flip());
            mesh.addVertexBuffer(pos);
        }

        IntBuffer data = ByteBuffer.allocateDirect(parsed.data.length<<2).order(ByteOrder.nativeOrder())
                .asIntBuffer().put(parsed.data);
        mesh.setData(data.flip());
        mesh.setIndices(0, parsed.data.length);

        return mesh;
    }
}
