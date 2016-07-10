package assets;

import cat.Cat;
import terrain.Terrain;
import terrain.TerrainField;
import utils.JsonUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by germangb on 10/07/16.
 */
public class TerrainAssetLoader implements AssetLoader<Terrain> {

    class JsonTerrain {
        int chunk_size;
        int max_height;
        String height;
    }

    @Override
    public Terrain load(String path, Object hints) throws Exception {
        InputStream is = Cat.files.getFile(path);
        InputStreamReader reader = new InputStreamReader(is);
        System.out.println(path);
        JsonTerrain terr = JsonUtils.fromJson(reader, JsonTerrain.class);
        is.close();

        is = Cat.files.getFile(terr.height);
        BufferedImage img = ImageIO.read(is);

        // create terrain
        Terrain terrain = new Terrain(img.getWidth(), terr.chunk_size);
        TerrainField height = terrain.getHeightField();
        int size = height.getSize();

        float[] values = height.getValues();

        // read height values
        for (int x = 0; x < size; ++x) {
            for (int z = 0; z < size; ++z) {
                int ind = height.getSize() * z + x;
                int red = (img.getRGB(x, z) >> 16) & 0xFF;
                values[ind] = red / 255f * 2 - 1;
                values[ind] *= terr.max_height;
            }
        }

        // filter heights
        int IT = 4;

        int[][] ker = {
                {4, 0, 6, 0, 4},
                {0, 0, 0, 0, 0},
                {0, 0, 8, 0, 6},
                {0, 0, 0, 0, 0},
                {4, 0, 6, 0, 4}
        };

        // filter image
        for (int i = 0; i < IT; ++i) {
            for (int x0 = 0; x0 < size; ++x0) {
                for (int z0 = 0; z0 < size; ++z0) {
                    int sum = ker[0][0];
                    int ind = toIndex(x0, z0, size);
                    values[ind] *= sum;

                    for (int x1 = 1; x1 < 5; ++x1) {
                        for (int z1 = 0; z1 < 5; ++z1) {
                            sum += ker[x1][z1];
                            if (x0+x1 < size && z0+z1 < size) {
                                values[ind] += values[toIndex(x0 + x1, z0 + z1, size)] * ker[x1][z1];
                            }
                        }
                    }

                    values[ind] /= sum;
                }
            }
        }

        is.close();
        return terrain;
    }

    private static int toIndex (int x, int z, int size) {
        return size * z + x;
    }
}
