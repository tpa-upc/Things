package terrain;

import graphics.*;
import math.Vector3f;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by germangb on 09/07/16.
 */
public class Terrain {

    private int[] indices;

    /** chunk size */
    private int chunk;

    /** Height field */
    private TerrainField height;

    /** Terrain fields */
    private Map<String, TerrainField> fields;

    /** Renderable data */
    private Mesh[][] meshes;

    public Terrain (int size, int chunk) {
        if (size % chunk != 0) {
            throw new IllegalArgumentException("chunk must divide size");
        }

        this.chunk = chunk;
        createIndices();

        this.meshes = new Mesh[size/chunk][size/chunk];
        this.height = new TerrainField(size);
        this.fields = new LinkedHashMap<>();
    }

    /**
     * Get a mesh from a specific chunk
     * @param x
     * @param z
     * @return
     */
    public Mesh getMesh (int x, int z) {
        if (meshes[x][z] == null) {
            meshes[x][z] = createMesh(x, z);
        }
        return meshes[x][z];
    }

    private void createIndices() {
        // vertex indices
        indices = new int[chunk*chunk*2*3];
        int i = 0;
        for (int r = 0; r < chunk; ++r) {
            for (int c = 0; c < chunk; ++c) {
                int v0 = r * (chunk + 1) + c;
                int v1 = v0 + 1;
                int v2 = v1 + chunk + 1;
                int v3 = v2-1;

                indices[i++] = v0;
                indices[i++] = v2;
                indices[i++] = v3;
                indices[i++] = v0;
                indices[i++] = v1;
                indices[i++] = v2;
            }
        }
    }

    private Mesh createMesh(int x, int z) {
        float[] posData = new float[(chunk+1)*(chunk+1)*3];
        float[] norData = new float[(chunk+1)*(chunk+1)*3];
        float[] uvData = new float[(chunk+1)*(chunk+1)*2];

        // collect attrs
        collectPositions(x, z, posData, norData, uvData);

        // create mesh
        Mesh mesh = new Mesh(Usage.STATIC);
        mesh.setPrimitive(Primitive.TRIANGLES);
        mesh.setIndices(0, indices.length);

        VertexBuffer posBuf = createBuffer(posData, Usage.STATIC);
        VertexBuffer norBuf = createBuffer(norData, Usage.STATIC);
        VertexBuffer uvBuf = createBuffer(uvData, Usage.STATIC);

        mesh.addVertexBuffer(Attribute.POSITION, posBuf);
        mesh.addVertexBuffer(Attribute.NORMAL, norBuf);
        mesh.addVertexBuffer(Attribute.UV, uvBuf);

        IntBuffer nio = ByteBuffer.allocateDirect(indices.length<<2)
                .order(ByteOrder.nativeOrder())
                .asIntBuffer()
                .put(indices);

        mesh.setData(nio.flip());

        return mesh;
    }

    private VertexBuffer createBuffer (float[] data, Usage usage) {
        VertexBuffer buffer = new VertexBuffer(usage);
        FloatBuffer nio = ByteBuffer.allocateDirect(data.length<<2)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(data);

        buffer.setData(nio.flip());
        return buffer;
    }

    private void collectPositions (int row, int col, float[] outPos, float[] outNor, float[] outUv) {
        Vector3f nor = new Vector3f();
        int i = 0, j = 0, k = 0;
        for (int r = 0; r < chunk+1; ++r) {
            for (int c = 0; c < chunk+1; ++c) {
                // position
                outPos[i++] = chunk * row + r;
                outPos[i++] = height.getValue(chunk * row + r, chunk * col + c);
                outPos[i++] = chunk * col + c;

                // normal
                float d = 2;
                nor.x = height.getValue(row*chunk+r+d, col*chunk+c) - height.getValue(row*chunk+r-d, col*chunk+c);
                nor.x = height.getValue(row*chunk+r, col*chunk+c+d) - height.getValue(row*chunk+r, col*chunk+c-d);
                nor.y = 2*d;
                nor.normalize();
                outNor[j++] = nor.x;
                outNor[j++] = nor.y;
                outNor[j++] = nor.z;

                // uv
                outUv[k++] = (float) (chunk * row + r) / height.getSize();
                outUv[k++] = (float) (chunk * col + c) / height.getSize();
            }
        }
    }

    /**
     * Create a field
     * @param name field name
     * @return
     */
    public TerrainField createField (String name) {
        int size = height.getSize();
        return fields.put(name, new TerrainField(size));
    }

    /**
     * Get field by name
     * @param name
     * @return
     */
    public TerrainField getField (String name) {
        return fields.get(name);
    }

    /**
     * Get height field
     * @return height field
     */
    public TerrainField getHeightField () {
        return height;
    }

    /**
     * Get terrain fields
     * @return
     */
    public Map<String, TerrainField> getFields () {
        return fields;
    }
}
