package assets;

import cat.Cat;
import com.google.gson.Gson;
import graphics.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.zip.GZIPInputStream;

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
        InputStream is = Cat.files.getFile(path);

        // check if file is gzipped
        if (hints != null && hints instanceof MeshHints && ((MeshHints) hints).gzip) {
            is = new GZIPInputStream(is);
        }

        Reader reader = new InputStreamReader(is);
        JsonMesh parsed = gson.fromJson(reader, JsonMesh.class);

        // create mesh
        Usage indUsage = Usage.STATIC;
        if (hints != null && hints instanceof MeshHints) {
            indUsage = ((MeshHints) hints).usage;
        }
        Mesh mesh = new Mesh(indUsage);

        Primitive prim = null;

        try {
            prim = Primitive.valueOf(parsed.primitive);
        } catch (Exception e) {
            throw new RuntimeException("Wrong primitive ("+parsed.primitive+")");
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

            // buffer usage
            Usage usage = Usage.STATIC;
            if (hints != null && hints instanceof MeshHints) {
                MeshHints mhint = (MeshHints) hints;
                if (mhint.bufferUsage.containsKey(attribute)) {
                    usage = mhint.bufferUsage.get(attribute);
                }
            }

            VertexBuffer pos = new VertexBuffer(usage);
            mesh.addVertexBuffer(attribute, pos);

            FloatBuffer posData = Cat.buffers.allocate(attr.data);
            pos.setData(posData);
        }

        IntBuffer data = Cat.buffers.allocate(parsed.data);

        mesh.setData(data);
        mesh.setIndices(0, parsed.data.length);

        return mesh;
    }
}
