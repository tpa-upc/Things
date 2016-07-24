package assets;

import cat.Cat;
import terrain.Terrain;
import terrain.TerrainField;
import utils.JsonUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by germangb on 10/07/16.
 */
public class TerrainAssetLoader implements AssetLoader<Terrain> {

    class JsonTerrain {
        int size;
        int chunk_size;
        int max_height;
        String height;
        String[] fields;
    }

    @Override
    public Terrain load(String path, Object hints) throws Exception {
        InputStream is = Cat.files.getFile(path);
        InputStreamReader reader = new InputStreamReader(is);
        //System.out.println(path);
        JsonTerrain terr = JsonUtils.fromJson(reader, JsonTerrain.class);
        is.close();

        is = Cat.files.getFile(terr.height);
        BufferedImage img = ImageIO.read(is);

        System.out.print("depth={ ");
        int[] ss = img.getColorModel().getComponentSize();
        for (int i = 0; i < ss.length; ++i)
            System.out.print(ss[i]+" ");
        System.out.println("}");

        // create terrain
        Terrain terrain = new Terrain(terr.size, img.getWidth(), terr.chunk_size);
        TerrainField height = terrain.getHeightField();
        //int size = height.getSize();
        int length = height.getLength();

        float[] values = height.getValues();
        short[] pixels = ((DataBufferUShort) img.getRaster().getDataBuffer()).getData();
        //System.out.println(Integer.toBinaryString(pixels[0]));

        // read height values
        int mask = 0xffff;
        for (int i = 0; i < pixels.length; ++i) {
            int val = pixels[i] & mask;
            values[i] = (float) val / mask * 2 - 1;
            values[i] *= terr.max_height;
        }

        // filter heights
        int IT = 0;

        int[][] ker = {
                {4, 0, 6, 0, 4},
                {0, 0, 0, 0, 0},
                {0, 0, 8, 0, 6},
                {0, 0, 0, 0, 0},
                {4, 0, 6, 0, 4}
        };

        // filter image
        for (int i = 0; i < IT; ++i) {
            for (int x0 = 0; x0 < length; ++x0) {
                for (int z0 = 0; z0 < length; ++z0) {
                    int sum = ker[0][0];
                    int ind = toIndex(x0, z0, length);
                    values[ind] *= sum;

                    for (int x1 = 1; x1 < 5; ++x1) {
                        for (int z1 = 0; z1 < 5; ++z1) {
                            if (x0+x1 < length && z0+z1 < length) {
                                sum += ker[x1][z1];
                                values[ind] += values[toIndex(x0 + x1, z0 + z1, length)] * ker[x1][z1];
                            }
                        }
                    }

                    values[ind] /= sum;
                }
            }
        }

        is.close();

        // create fields
        for (int i = 0; i < terr.fields.length; ++i) {
            InputStream fis = Cat.files.getFile(terr.fields[i]);
            BufferedImage fieldImg = ImageIO.read(fis);

            System.out.println(terr.fields[i]);

            TerrainField field = terrain.createField("field_"+i);
            float[] vals = field.getValues();

            // read field
            for (int x = 0; x < length; ++x) {
                for (int z = 0; z < length; ++z) {
                    int ind = height.getLength() * z + x;
                    int red = fieldImg.getRGB(x, z) & 0xFF;
                    vals[ind] = red / 255f * 2 - 1;
                    //System.out.println(red);
                }
            }

            fis.close();
        }

        return terrain;
    }

    private static int toIndex (int x, int z, int size) {
        return size * z + x;
    }
}
