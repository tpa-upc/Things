package manager;

import cat.Cat;
import component.Camera;
import component.Geometry;
import component.Transform;
import graphics.*;
import math.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by germangb on 19/06/16.
 */
public class Render extends Manager {

    /** Camera */
    Camera camera;

    /** Geometry to be drawn */
    List<Geometry> geometry = new ArrayList<>();

    // matrices
    Matrix4f vp = new Matrix4f();
    Matrix4f mvp = new Matrix4f();

    // rendering stuff
    RenderState state = new RenderState();
    ShaderProgram program;

    @Override
    public void onInit() {
        createProgram();
        state.depthTest = DepthTest.DISABLED;
        state.mode = PolygonMode.WIREFRAME;
    }

    private void createProgram() {
        //language=GLSL
        String vert = "#version 130\n" +
                "\n" +
                "in vec3 a_position;\n" +
                "\n" +
                "uniform mat4 u_mvp;\n" +
                "\n" +
                "void main () {\n" +
                "    gl_Position = u_mvp * vec4(a_position, 1.);\n" +
                "}";
        //language=GLSL
        String frag = "#version 130\n" +
                "\n" +
                "out vec4 frag_color;\n" +
                "\n" +
                "void main () {\n" +
                "    frag_color = vec4(1., 0., 1., 0.);\n" +
                "}";
        program = new ShaderProgram(vert, frag, new Attribute[] {
                Attribute.POSITION
        });
    }

    @Override
    public void onUpdate() {
        Cat.renderer.clearBuffers(BufferType.COLOR, BufferType.DEPTH);
        Cat.renderer.setState(state);
        Cat.renderer.bindProgram(program);

        if (camera != null) {
            camera.updateTransforms();
            vp.set(camera.viewProjection);
        } else {
            vp.identity();
        }

        for (Geometry geo : geometry) {
            // compute mvp
            Transform trans = geo.getThing().getComponent(Transform.class);
            mvp.set(vp).mul(trans.model);
            program.setUniform("u_mvp", mvp);

            Mesh mesh = geo.getMesh();
            Cat.renderer.renderMesh(mesh);
        }
    }

    /**
     * Add a geometry component to the list of geometry
     * @param geo Geometry component
     */
    public void addGeometry (Geometry geo) {
        geometry.add(geo);
    }

    /**
     * Remove component from the list of geometry
     * @param geo Geometry component
     */
    public void removeGeometry (Geometry geo) {
        geometry.remove(geo);
    }

    /**
     * Set the renderer camera
     * @param camera camera used in renderer
     */
    public void setCamera (Camera camera) {
        this.camera = camera;
    }

    /**
     * Returns the camera in the renderer
     * @return camera
     */
    public Camera getCamera () {
        return camera;
    }
}
