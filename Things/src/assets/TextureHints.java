package assets;

import graphics.TextureFilter;
import graphics.TextureWrap;

/**
 * Created by germangb on 20/06/16.
 */
public class TextureHints {

    public TextureFilter minFilter = TextureFilter.BILINEAR;
    public TextureFilter magFilter = TextureFilter.BILINEAR;
    public TextureWrap wrapU = TextureWrap.REPEAT;
    public TextureWrap wrapV = TextureWrap.REPEAT;
}
