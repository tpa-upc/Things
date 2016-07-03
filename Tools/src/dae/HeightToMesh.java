package dae;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import com.google.gson.Gson;
import dae.Common.JsonMesh;
import dae.Common.JsonAttribute;
import dae.Common.JsonMeta;

/**
 * Split heightmap image into meshes
 * Created by germangb on 03/07/16.
 */
public class HeightToMesh {

    static int SIZE = 1024;
    static int CHUNK = 128;

    public void convert (BufferedImage image, String out) throws Exception {
        int rows = SIZE / CHUNK;
        int cols = SIZE / CHUNK;

        float[][] chunkHeight = new float[CHUNK][CHUNK];
        float[] posData = new float[CHUNK*CHUNK*3];

        JsonMeta meta = new JsonMeta();
        meta.tool = "height-2-mesh-0";
        meta.name = "height_map_meshes0";
        meta.author = "germangb";

        Gson gson = new Gson();

        int[] indices = new int[(CHUNK-1)*(CHUNK-1)*2*3];
        int i = 0;
        for (int r = 0; r < CHUNK-1; ++r) {
            for (int c = 0; c < CHUNK-1; ++c) {
                int v0 = r * CHUNK + c;
                int v1 = v0 + 1;
                int v2 = v1 + CHUNK;
                int v3 = v2-1;

                indices[i++] = v0;
                indices[i++] = v3;
                indices[i++] = v2;

                indices[i++] = v0;
                indices[i++] = v2;
                indices[i++] = v1;
            }
        }

        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                JsonMesh mesh = new JsonMesh();
                mesh.meta = meta;
                mesh.primitive = "TRIANGLES";
                mesh.data = indices;

                collectHeights(row, col, image, chunkHeight);
                collectPositions(row, col, chunkHeight, posData);

                JsonAttribute pos = new JsonAttribute();
                pos.data = posData;
                pos.name = "POSITION";

                mesh.attributes = new JsonAttribute[] { pos };


                String file = "height_"+row+"_"+col+".json";
                BufferedWriter writer = new BufferedWriter(new FileWriter(out+"/"+file));
                gson.toJson(mesh, writer);
                writer.flush();
                writer.close();
            }
        }
    }

    public void collectPositions (int row, int col, float[][] height, float[] out) {
        int i = 0;
        for (int r = 0; r < CHUNK; ++r) {
            for (int c = 0; c < CHUNK; ++c) {
                out[i++] = CHUNK * row + r;
                out[i++] = height[r][c];
                out[i++] = CHUNK * col + c;
            }
        }
    }

    public void collectHeights (int row, int col, BufferedImage image, float[][] out) {
        for (int i = 0; i < CHUNK; ++i) {
            for (int x = 0; x < CHUNK; ++x) {
                int pixelRow = CHUNK * row + i;
                int pixelCol = CHUNK * col + x;

                int argb = image.getRGB(pixelRow, pixelCol);
                int red = (argb >> 24) & 0xFF;
                float height = (float) red / 255 * 2 - 1;

                out[i][x] = height;
            }
        }
    }

    public static void main (String[] argv) throws Exception {
        BufferedImage image = ImageIO.read(new File("heightmap.png"));
        new HeightToMesh().convert(image, "map/");
    }
}
