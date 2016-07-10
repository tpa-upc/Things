package graphics;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by germangb on 17/06/16.
 */
public class ShaderProgram {

    /** destroy flag */
    private boolean destroy = false;

    /** Vertex shader source */
    private String vert;

    /** Fragment shader source */
    private String frag;

    /** Attributes used by shader */
    private Attribute[] attributes;

    /** Uniforms */
    private HashMap<String, Object> uniforms;

    public ShaderProgram (String vert, String frag, Attribute ... attributes) {
        this.vert = vert;
        this.frag = frag;
        this.attributes = attributes;
        this.uniforms = new HashMap<>();
    }

    /**
     * Get attributes used by shader
     * @return attributes array
     */
    public Attribute[] getAttributes () {
        return attributes;
    }

    /**
     * Set a uniform
     * @param name uniform name
     * @param value uniform value
     */
    public void setUniform (String name, Object value) {
        uniforms.put(name, value);
    }

    /**
     * Get map of uniforms
     * @return uniforms
     */
    public Map<String, Object> getUniforms () {
        return uniforms;
    }

    /**
     * Get vertex shader source
     * @return vertex shader
     */
    public String getVertexSource() {
        return vert;
    }

    /**
     * Get fragment shader source
     * @return fragment shader
     */
    public String getFragmentShader() {
        return frag;
    }

    /**
     * Destroy program
     */
    public void destroy () {
        destroy = true;
    }

    /**
     * Check destroy flag
     * @return
     */
    public boolean isDestroy() {
        return destroy;
    }
}
