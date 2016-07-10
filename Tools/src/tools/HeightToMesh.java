package tools;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import com.google.gson.Gson;
import tools.Common.JsonMesh;
import tools.Common.JsonAttribute;
import tools.Common.JsonMeta;
import math.Vector3f;

/**
 * Split heightmap image into meshes
 * Created by germangb on 03/07/16.
 */
public class HeightToMesh {

    static int SIZE = 1024;
    static int CHUNK = 128;
    static int HEIGHT = 24;

    public void convert (BufferedImage image, String out) throws Exception {
        int rows = SIZE / CHUNK;
        int cols = SIZE / CHUNK;

        float[][] height = new float[SIZE+1][SIZE+1];
        float[][] heightAux = new float[SIZE+1][SIZE+1];
        Vector3f[][] normals = new Vector3f[SIZE+1][SIZE+1];

        for (int i = 0; i < SIZE; ++i) {
            for (int x = 0; x < SIZE; ++x) {
                int pixel = (image.getRGB(i, x) >> 16) & 0xff;
                height[i][x] = (float) pixel / 255 * 2 - 1;
                height[i][x] *= HEIGHT;
            }
        }

        for (int i = 0; i < SIZE+1; ++i) {
            height[SIZE][i] = height[SIZE-1][i];
            height[i][SIZE] = height[i][SIZE-1];
        }

        for (int i = 0; i < SIZE+1; ++i) {
            for (int x = 0; x < SIZE+1; ++x) {
                normals[i][x] = new Vector3f();
            }
        }

        // filter
        float[][] filt = new float[][] {
                {4, 0, 6, 0, 4},
                {0, 0, 0, 0, 0},
                {6, 0, 8, 0, 6},
                {0, 0, 0, 0, 0},
                {4, 0, 6, 0, 4}
        };

        // filter several times
        for (int s = 0; s < 2; ++s) {
            for (int i = 0; i < SIZE; ++i) {
                for (int x = 0; x < SIZE; ++x) {
                    float sum = 0;
                    for (int ii = 0; ii < filt.length; ++ii) {
                        for (int xx = 0; xx < filt[ii].length; ++xx) {
                            sum += filt[ii][xx];
                            heightAux[i][x] += height[Math.min(i + ii, SIZE)][Math.min(x + xx, SIZE)] * filt[ii][xx];
                        }
                    }
                    heightAux[i][x] /= sum;
                }
            }

            height = heightAux;
        }

        // compute normals
        for (int i = 0; i < SIZE; ++i) {
            for (int x = 0; x < SIZE; ++x) {
                Vector3f n = normals[i][x];
                n.x = height[i][Math.min(x + 2, SIZE)] - height[i][Math.max(x - 2, 0)];
                n.z = height[Math.min(i + 2, SIZE)][x] - height[Math.max(i - 2, 0)][x];
                n.y = 4;
                //n.set(0, 1, 0);
                n.normalize();
                //System.out.println(n);
            }
        }

        JsonMeta meta = new JsonMeta();
        meta.tool = "height-2-mesh-0";
        meta.name = "height_map_meshes0";
        meta.author = "germangb";

        Gson gson = new Gson();

        int[] indices = new int[CHUNK*CHUNK*2*3];
        int i = 0;
        for (int r = 0; r < CHUNK; ++r) {
            for (int c = 0; c < CHUNK; ++c) {
                int v0 = r * (CHUNK + 1) + c;
                int v1 = v0 + 1;
                int v2 = v1 + CHUNK + 1;
                int v3 = v2-1;

                indices[i++] = v0;
                indices[i++] = v2;
                indices[i++] = v3;

                indices[i++] = v0;
                indices[i++] = v1;
                indices[i++] = v2;
            }
        }

        float[] posData = new float[(CHUNK+1)*(CHUNK+1)*3];
        float[] norData = new float[(CHUNK+1)*(CHUNK+1)*3];
        float[] uvData = new float[(CHUNK+1)*(CHUNK+1)*2];

        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                JsonMesh mesh = new JsonMesh();
                mesh.meta = meta;
                mesh.primitive = "TRIANGLES";
                mesh.data = indices;

                collectPositions(row, col, height, posData);
                collectNormals(row, col, normals, norData);
                collectUv(row, col, uvData);

                JsonAttribute pos = new JsonAttribute();
                pos.data = posData;
                pos.name = "POSITION";

                JsonAttribute nor = new JsonAttribute();
                nor.data = norData;
                nor.name = "NORMAL";

                JsonAttribute uv = new JsonAttribute();
                uv.data = uvData;
                uv.name = "UV";

                mesh.attributes = new JsonAttribute[] { pos, nor, uv };

                String file = "height_"+row+"_"+col+".json";
                BufferedWriter writer = new BufferedWriter(new FileWriter(out+"/"+file));
                gson.toJson(mesh, writer);
                writer.flush();
                writer.close();
            }
        }
    }

    private void collectUv(int row, int col, float[] out) {
        int i = 0;
        for (int r = 0; r < CHUNK+1; ++r) {
            for (int c = 0; c < CHUNK+1; ++c) {
                out[i++] = (float) (CHUNK * row + r) / SIZE;
                out[i++] = (float) (CHUNK * col + c) / SIZE;
            }
        }
    }

    private void collectNormals(int row, int col, Vector3f[][] normals, float[] out) {
        int i = 0;
        for (int r = 0; r < CHUNK+1; ++r) {
            for (int c = 0; c < CHUNK+1; ++c) {
                Vector3f n = normals[CHUNK * row + r][CHUNK * col + c];
                out[i++] = n.x;
                out[i++] = n.y;
                out[i++] = n.z;
            }
        }
    }

    public void collectPositions (int row, int col, float[][] height, float[] out) {
        int i = 0;
        for (int r = 0; r < CHUNK+1; ++r) {
            for (int c = 0; c < CHUNK+1; ++c) {
                out[i++] = r;
                out[i++] = height[CHUNK * row + r][CHUNK * col + c];
                out[i++] = c;
            }
        }
    }

    public static void main (String[] argv) throws Exception {
        BufferedImage image = ImageIO.read(new File("heightmap.png"));
        new HeightToMesh().convert(image, "map/");
    }
}
