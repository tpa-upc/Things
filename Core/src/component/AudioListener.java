package component;

import cat.Cat;
import math.Matrix4f;
import math.Vector3f;
import math.Vector4f;

/**
 * Created by germangb on 27/06/16.
 */
public class AudioListener extends Component {

    private Vector4f aux = new Vector4f();
    private Vector3f look = new Vector3f();
    private Vector3f up = new Vector3f();

    @Override
    public void onUpdate() {
        // update listener's position and orientation
        Matrix4f trans = getThing().getTransform().model;

        // position vector
        Vector3f pos = getThing().getTransform().position;

        // look vector
        aux = trans.getColumn(2, aux);
        look.set(aux.x, aux.y, aux.z);

        // up vector
        aux = trans.getColumn(1, aux);
        up.set(aux.x, aux.y, aux.z);

        // update listener position & orientation
        Cat.audioRenderer.setListener(pos, look, up);
    }
}
