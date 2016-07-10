package tools;

import com.google.gson.Gson;
import math.Matrix4f;
import math.Quaternionf;
import math.Vector3f;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import tools.Common.JsonMesh;
import tools.Common.JsonAttribute;
import tools.Common.JsonMeta;

/**
 * Created by germangb on 29/06/16.
 */
public class ColladaToJson {

    class JsonJoint {
        int parent;
        String name;
        float[] translate;
        float[] rotate;
        float[] scale;
    }

    class JsonSkeleton {
        JsonMeta meta;
        JsonJoint[] joints;
    }

    public void convert (Reader input, Writer outMesh, Writer outSkel) throws Exception {
        // read data
        BufferedReader in = new BufferedReader(input);

        // XML stack
        Stack<String> prop = new Stack<>();

        // arrays
        List<String> attrNames = new ArrayList<>();
        List<List<Float>> attrs = new ArrayList<>();

        List<Integer> indices = null;

        String line = null;

        Stack<JsonJoint> jointStack = new Stack<>();
        Stack<Integer> jointId = new Stack<>();
        List<JsonJoint> jointList = new ArrayList<>();
        int j = 0;

        // quick fix
        boolean ignoreNode = false;

        while ((line = in.readLine()) != null) {
            line = line.trim().toLowerCase();

            if (line.startsWith("</")) {
                if (line.startsWith("</node>")) {
                    if (ignoreNode) {
                        ignoreNode = false;
                        continue;
                    }

                    // check if still reading skeleton
                    if (!jointStack.isEmpty()) {
                        jointStack.pop();
                        jointId.pop();
                    }
                }
            } else if (line.startsWith("<")) {
                if (ignoreNode)
                    continue;

                // find out xml tag
                if (line.startsWith("<float_array")) {
                    // read vertex attribute list
                    //System.out.println(line.substring(0, 64));
                    List<Float> array = readArray(line);
                    attrs.add(array);

                    if (line.contains("-positions")) {
                        attrNames.add("POSITION");

                        /*float minX = 99999, minY = 99999, minZ = 9999;
                        for (int k = 0; k < array.size(); k += 3) {
                            minX = Math.min(minX, array.get(k+0));
                            minY = Math.min(minY, array.get(k+1));
                            minZ = Math.min(minZ, array.get(k+2));
                        }
                        System.out.println(minX+" "+minY+" "+minZ);*/
                    }
                    else if (line.contains("-normals")) attrNames.add("NORMAL");
                    else if (line.contains("-tangents")) attrNames.add("TANGENT");
                    else if (line.contains("-bitangents")) attrNames.add("BITANGENT");
                    else if (line.contains("-texcoord")) attrNames.add("UV");
                } else if (line.startsWith("<node")) {
                    // parse skeleton nodes
                    if (line.contains("id=\"armature\"")) {
                        // root node
                        JsonJoint joint = new JsonJoint();
                        jointStack.push(joint);
                        jointId.push(j++);
                        jointList.add(joint);

                        joint.parent = -1;

                        // root transformations
                        joint.rotate = new float[]{0, 0, 0, 1};
                        joint.scale = new float[]{1, 1, 1};
                        joint.translate = new float[]{0, 0, 0};
                    } else if (line.contains("type=\"joint\"")) {
                        // parented node
                        JsonJoint joint = new JsonJoint();

                        // get joint parent (current stack top)
                        joint.parent = jointId.peek();
                        //System.out.println(joint.parent);

                        // add to list and push to stack
                        jointStack.push(joint);
                        jointId.push(j++);
                        jointList.add(joint);

                        // root transformations
                        joint.rotate = new float[]{0, 0, 0, 1};
                        joint.scale = new float[]{1, 1, 1};
                        joint.translate = new float[]{0, 0, 0};
                    } else {
                        // skip this node
                        ignoreNode = true;
                    }
                } else if (line.startsWith("<matrix")) {
                    //System.out.println(jointId.peek());
                    // still reading skeleton?
                    if (!jointStack.empty()) {
                        // read rows { row0, row1, row2, row3 }
                        List<Float> array = readArray(line);
                        float[] vals = new float[16];
                        for (int i = 0; i < 16; ++i)
                            vals[i] = array.get(i);

                        Matrix4f matrix = new Matrix4f();
                        matrix.set(vals).transpose();

                        // get rotation, translation & scale
                        Quaternionf q = new Quaternionf();
                        Vector3f t = new Vector3f();
                        Vector3f s = new Vector3f(1);

                        JsonJoint joint = jointStack.peek();

                        matrix.getNormalizedRotation(q);
                        matrix.getTranslation(t);
                        matrix.getScale(s);

                        joint.translate[0] = t.x;
                        joint.translate[1] = t.y;
                        joint.translate[2] = t.z;

                        joint.rotate[0] = q.x;
                        joint.rotate[1] = q.y;
                        joint.rotate[2] = q.z;
                        joint.rotate[3] = q.w;

                        joint.scale[0] = s.x;
                        joint.scale[1] = s.y;
                        joint.scale[2] = s.z;
                    }
                } else if (line.startsWith("<p>")) {
                    indices = readIndices(line);
                } else {
                    //System.out.println(line);

                    // tag we don't care about
                    prop.push("meow");
                }
            }
        }

        in.close();

        // create Json Object
        JsonMesh mesh = new JsonMesh();
        mesh.meta = new JsonMeta();
        mesh.primitive = "TRIANGLES";

        mesh.attributes = new JsonAttribute[attrNames.size()];
        //System.out.println(attrs.size()+" "+attrNames.size());
        for (int i = 0; i < attrNames.size(); ++i) {
            //System.out.println(attrNames.get(i));
            mesh.attributes[i] = new JsonAttribute();
            mesh.attributes[i].name = attrNames.get(i);

            List<Float> data = attrs.get(i);
            mesh.attributes[i].data = new float[data.size()];
            for (int k = 0; k < data.size(); ++k) {
                mesh.attributes[i].data[k] = data.get(k);
            }
        }

        mesh.data = new int[indices.size()];
        for (int k = 0; k < indices.size(); ++k) {
            mesh.data[k] = indices.get(k);
        }

        // write json file
        Gson gson = new Gson();

        // write mesh
        BufferedWriter out = new BufferedWriter(outMesh);
        gson.toJson(mesh, out);
        out.flush();
        out.close();

        // write skeleton
        if (outSkel != null) {
            JsonSkeleton skeleton = new JsonSkeleton();
            skeleton.meta = new JsonMeta();
            skeleton.meta.name = "awesome skeleton!! :)";
            skeleton.joints = jointList.toArray(new JsonJoint[j]);

            out = new BufferedWriter(outSkel);
            gson.toJson(skeleton, out);
            out.flush();
            out.close();
        }
    }

    private List<Integer> readIndices (String line) {
        List<Integer> indices = new ArrayList<>();

        int from = line.indexOf('>');
        int to = line.indexOf('<', from);
        String[] numbers = line.substring(from+1, to)
                .trim()
                .split("\\s+");

        //System.out.println(numbers.length);
        for (int i = 0; i < numbers.length; i++) {
            indices.add(Integer.parseInt(numbers[i]));
        }

        return indices;
    }

    private List<Float> readArray (String line) {
        List<Float> list = new ArrayList<>();

        int from = line.indexOf('>');
        int to = line.indexOf('<', from);
        String[] numbers = line.substring(from+1, to)
                .trim()
                .split("\\s+");

        //System.out.println(numbers.length);
        for (int i = 0; i < numbers.length; i++) {
            list.add(Float.parseFloat(numbers[i]));
        }

        return list;
    }

    public static void main (String[] args) {
        try {
            String input = "scene.tools";
            String meshFile = "scene.json";
            String skelFile = "scene-skeleton.json";

            Reader in = new FileReader(input);
            Writer mesh = new FileWriter(meshFile);
            Writer skel = new FileWriter(skelFile);
            new ColladaToJson().convert(in, mesh, skel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
