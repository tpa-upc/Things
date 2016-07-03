package assets;

import cat.Cat;
import com.google.gson.Gson;
import fonts.BitmapFont;
import fonts.GlyphInfo;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by germangb on 21/06/16.
 */
public class BitmapFontAssetLoader implements AssetLoader<BitmapFont> {

    class JsonCommon {
        int lineHeight;
        int base;
    }

    class JsonGlyph {
        int id;
        int x, y;
        int width, height;
        int xoffset, yoffset;
        int xadvance;
    }

    class JsonFont {
        JsonCommon common;
        JsonGlyph[] chars;
    }

    @Override
    public BitmapFont load(String path, Object hints) throws Exception {
        Gson gson = new Gson();
        InputStream is = Cat.files.getFile(path);
        JsonFont font = gson.fromJson(new InputStreamReader(is), JsonFont.class);

        // create info
        BitmapFont info = new BitmapFont();
        info.setBase(font.common.base);
        info.setLineHeight(font.common.lineHeight);

        for (int i = 0; i < font.chars.length; ++i) {
            JsonGlyph ch = font.chars[i];
            GlyphInfo ginf = new GlyphInfo();
            ginf.setId(ch.id);
            ginf.setX(ch.x);
            ginf.setY(ch.y);
            ginf.setWidth(ch.width);
            ginf.setHeight(ch.height);
            ginf.setXoffset(ch.xoffset);
            ginf.setYoffset(ch.yoffset);
            ginf.setXadvance(ch.xadvance);

            info.getGlyphs().put(ch.id, ginf);
        }

        return info;
    }
}
