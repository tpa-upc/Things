package fonts;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by germangb on 21/06/16.
 */
public class BitmapFont {

    /** line height */
    private int lineHeight;

    /** character height */
    private int base;

    /** Glyph info */
    private Map<Integer, GlyphInfo> glyphs = new HashMap<>();

    public BitmapFont() {
    }

    /**
     * Glyph info getter
     * @return
     */
    public Map<Integer, GlyphInfo> getGlyphs() {
        return glyphs;
    }

    /**
     * Line height
     * @return
     */
    public int getLineHeight () {
        return lineHeight;
    }

    /**
     * Line height setter
     * @param lineHeight
     */
    public void setLineHeight(int lineHeight) {
        this.lineHeight = lineHeight;
    }

    /**
     * Base getter
     * @return
     */
    public int getBase() {
        return base;
    }

    /**
     * Base setter
     * @param base
     */
    public void setBase(int base) {
        this.base = base;
    }
}
