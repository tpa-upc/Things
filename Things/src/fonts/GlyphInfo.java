package fonts;

/**
 * Created by germangb on 21/06/16.
 */
public class GlyphInfo {

    /** GlyphInfo id */
    private int id = 0;

    // texture location info
    private int x = 0;
    private int y = 0;
    private int width = 0;
    private int height = 0;

    // rendering info
    private int xoffset = 0;
    private int yoffset = 0;
    private int xadvance = 0;

    public GlyphInfo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getXoffset() {
        return xoffset;
    }

    public void setXoffset(int xoffset) {
        this.xoffset = xoffset;
    }

    public int getYoffset() {
        return yoffset;
    }

    public void setYoffset(int yoffset) {
        this.yoffset = yoffset;
    }

    public int getXadvance() {
        return xadvance;
    }

    public void setXadvance(int xadvance) {
        this.xadvance = xadvance;
    }
}
