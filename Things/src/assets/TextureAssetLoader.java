package assets;

import cat.Cat;
import graphics.Texture;
import graphics.TextureFormat;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by germangb on 20/06/16.
 */
public class TextureAssetLoader implements AssetLoader<Texture> {

    @Override
    public Texture load(String path, Object hints) throws Exception {
        InputStream is = Cat.files.getFile(path);
        BufferedImage im = ImageIO.read(is);

        // texture format
        boolean alpha = im.getColorModel().hasAlpha();
        TextureFormat format = alpha ? TextureFormat.RGBA : TextureFormat.RGB;

        // create texture
        int width = im.getWidth();
        int height = im.getHeight();
        Texture tex = new Texture(width, height, format);

        // check hints
        if (hints != null && hints instanceof TextureHints) {
            TextureHints th = (TextureHints) hints;
            if (th.minFilter != null) tex.setMinFilter(th.minFilter);
            if (th.magFilter != null) tex.setMagFilter(th.magFilter);
            if (th.wrapU != null) tex.setWrapU(th.wrapU);
            if (th.wrapV != null) tex.setWrapV(th.wrapV);
        }

        // read data
        int ncomp = alpha ? 4 : 3;
        ByteBuffer pixels = Cat.buffers.allocate(width*height*ncomp);
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                int argb = im.getRGB(x, y);
                byte red = (byte) ((argb >> 16) & 0xff);
                byte gre = (byte) ((argb >> 8) & 0xff);
                byte blu = (byte) (argb & 0xff);
                pixels.put(new byte[] {red, gre, blu});
                if (alpha) {
                    byte alp = (byte)((argb >> 24) & 0xff);
                    pixels.put(alp);
                }
            }
        }

        tex.setData(pixels.flip());
        return tex;
    }
}
