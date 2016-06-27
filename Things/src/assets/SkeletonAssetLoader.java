package assets;

import animation.Joint;
import animation.Skeleton;
import cat.Cat;
import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by germangb on 27/06/16.
 */
public class SkeletonAssetLoader implements AssetLoader<Skeleton> {

    class JsonJoint {
        int parent;
        String name;

        float[] translate;
        float[] rotation;
        float[] scale;
    }

    class JsonSkeleton {
        JsonJoint[] joints;
    }

    @Override
    public Skeleton load(String path, Object hints) throws Exception {
        InputStreamReader read = new InputStreamReader(Cat.files.getFile(path));
        Gson gson = new Gson();

        JsonSkeleton skel = gson.fromJson(read, JsonSkeleton.class);
        read.close();

        Skeleton skeleton = new Skeleton();
        List<Joint> soFar = skeleton.getJoints();   // not modified directly

        // build joints
        for (int i = 0; i < skel.joints.length; ++i) {
            JsonJoint jo = skel.joints[i];
            Joint joint = new Joint(jo.parent < 0 ? null :soFar.get(jo.parent), jo.name);
            joint.translate.set(jo.translate[0], jo.translate[1], jo.translate[2]);
            joint.rotation.set(jo.rotation[0], jo.rotation[1], jo.rotation[2], jo.rotation[3]);
            joint.scale.set(jo.scale[0], jo.scale[1], jo.scale[2]);
            joint.update();

            // insert in skeleton
            skeleton.addJoint(joint);
        }

        return skeleton;
    }
}
