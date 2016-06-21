package component;

import math.Matrix4f;
import math.Quaternionf;
import math.Vector3f;

/**
 * Created by germangb on 19/06/16.
 */
public class Transform extends Component {

    /** Component Position */
    public final Vector3f position = new Vector3f(0f);

    /** Component Scale */
    public final Vector3f scale = new Vector3f(1f);

    /** Component Rotation */
    public final Quaternionf rotation = new Quaternionf();

    /** Transformation (scale + rotation + translation) */
    public final Matrix4f model = new Matrix4f();

    @Override
    public void onUpdate() {
        // compute model transformation
        model.identity()
                .translate(position)
                .scale(scale)
                .rotate(rotation);
    }
}
