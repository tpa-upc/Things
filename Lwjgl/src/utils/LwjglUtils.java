package utils;

import audio.AudioFormat;
import graphics.*;
import input.Key;
import math.*;

import java.nio.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_MENU;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SUPER;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.openal.AL10.*;

/**
 * Created by germangb on 17/06/16.
 */
public class LwjglUtils {

    public static FloatBuffer mat = ByteBuffer.allocateDirect(16<<2).order(ByteOrder.nativeOrder()).asFloatBuffer();

    /** GLSL program vars */
    public static class GLSLProgram {
        public int program;
        public int vertex;
        public int fragment;
    }

    public static GLSLProgram createProgram (ShaderProgram program) {
        GLSLProgram prog = new GLSLProgram();

        // compile shaders
        prog.vertex = createShader(GL_VERTEX_SHADER, program.getVertexSource());
        prog.fragment = createShader(GL_FRAGMENT_SHADER, program.getFragmentShader());

        // create program and attach shaders
        prog.program = glCreateProgram();
        glAttachShader(prog.program, prog.vertex);
        glAttachShader(prog.program, prog.fragment);

        // bind attributes
        Attribute[] attr = Attribute.values();
        for (int i = 0; i < attr.length; ++i) {
            int id = attr[i].id;
            String name = attr[i].name;
            //System.out.println(name);
            glBindAttribLocation(prog.program, id, name);
        }

        // link program
        glLinkProgram(prog.program);
        return prog;
    }

    private static int createShader(int type, String source) {
        int shader = glCreateShader(type);
        glShaderSource(shader, source);
        glCompileShader(shader);
        String log = glGetShaderInfoLog(shader);

        int status = glGetShaderi(shader, GL_COMPILE_STATUS);
        if (status == GL_FALSE)
            throw new RuntimeException(log);

        return shader;
    }

    public static void uniform (int loc, Object val) {
        if (val instanceof Integer) {
            glUniform1i(loc, (int) val);
        } else if (val instanceof Float) {
            glUniform1f(loc, (float) val);
        } else if (val instanceof Vector2f) {
            Vector2f v = (Vector2f) val;
            glUniform2f(loc, v.x, v.y);
        } else if (val instanceof Vector3f) {
            Vector3f v = (Vector3f) val;
            glUniform3f(loc, v.x, v.y, v.z);
        } else if (val instanceof Vector4f) {
            Vector4f v = (Vector4f) val;
            glUniform4f(loc, v.x, v.y, v.z, v.w);
        } else if (val instanceof Matrix4f) {
            ((Matrix4f) val).get(mat);
            glUniformMatrix4fv(loc, false, mat);
        } else if (val instanceof Matrix3f) {
            ((Matrix3f) val).get(mat);
            glUniformMatrix3fv(loc, false, mat);
        }
    }

    public static int usage (Usage usage) {
        switch (usage) {
            case STATIC: return GL_STATIC_DRAW;
            case DYNAMIC: return GL_DYNAMIC_DRAW;
            case STREAM: return GL_STREAM_DRAW;
            default:
                throw new RuntimeException("This should never be reached");
        }
    }

    public static int primitive (Primitive prim) {
        switch (prim) {
            case TRIANGLES: return GL_TRIANGLES;
            case TRIANGLE_FAN: return GL_TRIANGLE_FAN;
            case TRIANGLE_STRIP: return GL_TRIANGLE_STRIP;
            case LINES: return GL_LINES;
            case LINE_LOOP: return GL_LINE_LOOP;
            case LINE_STRIP: return GL_LINE_STRIP;
            case POINTS: return GL_POINTS;
            default:
                throw new RuntimeException("This should never be reached");
        }
    }

    public static int textureFilter (TextureFilter filt) {
        switch (filt) {
            case BILINEAR: return GL_LINEAR;
            case NEAREST: return GL_NEAREST;
            default:
                throw new RuntimeException("This should never be reached");
        }
    }

    public static int textureWrap (TextureWrap wrap) {
        switch (wrap) {
            case REPEAT: return GL_REPEAT;
            case CLAMP: return GL_CLAMP;
            default:
                throw new RuntimeException("This should never be reached");
        }
    }

    public static int textureFormat (TextureFormat format) {
        switch (format) {
            case RGB: return GL_RGB;
            case RGBA: return GL_RGBA;
            case RED: return GL_RED;
            case DEPTH: return GL_DEPTH_COMPONENT;
            default:
                throw new RuntimeException("This should never be reached");
        }
    }

    public static int audioFormat (AudioFormat format) {
        switch (format) {
            case MONO8: return AL_FORMAT_MONO8;
            case MONO16: return AL_FORMAT_MONO16;
            case STEREO8: return AL_FORMAT_STEREO8;
            case STEREO16: return AL_FORMAT_STEREO16;
            default:
                throw new RuntimeException("This should never be reached");
        }
    }

    public static void setCulling (Culling cull) {
        switch (cull) {
            case DISABLED:
                glDisable(GL_CULL_FACE);
                break;
            case BACK:
                glEnable(GL_CULL_FACE);
                glCullFace(GL_BACK);
                break;
            case FRONT:
                glEnable(GL_CULL_FACE);
                glCullFace(GL_FRONT);
                break;
            default:
                throw new RuntimeException("This should never be reached");
        }
    }

    public static void setBlending (Blending blend) {
        switch (blend) {
            case DISABLED:
                glDisable(GL_BLEND);
                break;
            case ALPHA:
                glEnable(GL_BLEND);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                break;
            case ADDITIVE:
                glEnable(GL_BLEND);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE);
                break;
            default:
                throw new RuntimeException("This should never be reached");
        }
    }

    public static void setDepthTest (DepthTest depth) {
        if (depth != DepthTest.DISABLED) {
            glEnable(GL_DEPTH_TEST);
        }

        switch (depth) {
            case DISABLED:
                glDisable(GL_DEPTH_TEST);
                break;
            case NEVER:
                glDepthFunc(GL_NEVER);
                break;
            case ALWAYS:
                glDepthFunc(GL_ALWAYS);
                break;
            case LESS:
                glDepthFunc(GL_LESS);
                break;
            case LEQUAL:
                glDepthFunc(GL_LEQUAL);
                break;
            case GREATER:
                glDepthFunc(GL_GREATER);
                break;
            case GEQUAL:
                glDepthFunc(GL_GEQUAL);
                break;
            case EQUAL:
                glDepthFunc(GL_EQUAL);
                break;
            case NOTEQUAL:
                glDepthFunc(GL_NOTEQUAL);
                break;
            default:
                throw new RuntimeException("This should never be reached");
        }
    }

    public static int getBufferBitmask (BufferType... bufs) {
        int mask = 0;
        for (int i = 0; i < bufs.length; ++i) {
            switch (bufs[i]) {
                case COLOR:
                    mask |= GL_COLOR_BUFFER_BIT;
                    break;
                case DEPTH:
                    mask |= GL_DEPTH_BUFFER_BIT;
                    break;
                case STENCIL:
                    mask |= GL_STENCIL_BUFFER_BIT;
                    break;
                default:
                    throw new RuntimeException("This should never be reached");
            }
        }

        return mask;
    }

    public static int renderMode (PolygonMode mode) {
        switch (mode) {
            case FILL: return GL_FILL;
            case WIREFRAME: return GL_LINE;
            default:
                throw new RuntimeException("This should never be reached");
        }
    }

    public static void bufferData (int target, Buffer data, Usage us) {
        int usage = LwjglUtils.usage(us);

        if (data instanceof FloatBuffer) {
            glBufferData(target, (FloatBuffer) data, usage);
        } else if (data instanceof DoubleBuffer) {
            glBufferData(target, (DoubleBuffer) data, usage);
        } else if (data instanceof IntBuffer) {
            glBufferData(target, (IntBuffer) data, usage);
        } else if (data instanceof ShortBuffer) {
            glBufferData(target, (IntBuffer) data, usage);
        } else if (data instanceof ByteBuffer) {
            glBufferData(target, (ByteBuffer) data, usage);
        }
    }

    public static void bufferSubData (int target, Buffer data) {
        if (data instanceof FloatBuffer) {
            glBufferSubData(target, 0, (FloatBuffer) data);
        } else if (data instanceof DoubleBuffer) {
            glBufferSubData(target, 0, (DoubleBuffer) data);
        } else if (data instanceof IntBuffer) {
            glBufferSubData(target, 0, (IntBuffer) data);
        } else if (data instanceof ShortBuffer) {
            glBufferSubData(target, 0, (ShortBuffer) data);
        } else if (data instanceof ByteBuffer) {
            glBufferSubData(target, 0, (ByteBuffer) data);
        }
    }

    public static int nioToSigned (Buffer buffer) {
        if (buffer instanceof FloatBuffer) return GL_FLOAT;
        else if (buffer instanceof DoubleBuffer) return GL_DOUBLE;
        else if (buffer instanceof IntBuffer) return GL_INT;
        else if (buffer instanceof ShortBuffer) return GL_SHORT;
        else if (buffer instanceof ByteBuffer) return GL_BYTE;
        throw new RuntimeException("This should never be reached");
    }

    public static int nioToUnsigned (Buffer buffer) {
        if (buffer instanceof FloatBuffer) return GL_FLOAT;
        else if (buffer instanceof DoubleBuffer) return GL_DOUBLE;
        else if (buffer instanceof IntBuffer) return GL_UNSIGNED_INT;
        else if (buffer instanceof ShortBuffer) return GL_UNSIGNED_SHORT;
        else if (buffer instanceof ByteBuffer) return GL_UNSIGNED_BYTE;
        throw new RuntimeException("This should never be reached");
    }

    public static int keyToGLFW (Key key) {
        int code = 0;
        switch (key) {
            case NUM_0: code = GLFW_KEY_0; break;
            case NUM_1: code = GLFW_KEY_1; break;
            case NUM_2: code = GLFW_KEY_2; break;
            case NUM_3: code = GLFW_KEY_3; break;
            case NUM_4: code = GLFW_KEY_4; break;
            case NUM_5: code = GLFW_KEY_5; break;
            case NUM_6: code = GLFW_KEY_6; break;
            case NUM_7: code = GLFW_KEY_7; break;
            case NUM_8: code = GLFW_KEY_8; break;
            case NUM_9: code = GLFW_KEY_9; break;

            case A: code = GLFW_KEY_A; break;
            case B: code = GLFW_KEY_B; break;
            case C: code = GLFW_KEY_C; break;
            case D: code = GLFW_KEY_D; break;
            case E: code = GLFW_KEY_E; break;
            case F: code = GLFW_KEY_F; break;
            case G: code = GLFW_KEY_G; break;
            case H: code = GLFW_KEY_H; break;
            case I: code = GLFW_KEY_I; break;
            case J: code = GLFW_KEY_J; break;
            case K: code = GLFW_KEY_K; break;
            case L: code = GLFW_KEY_L; break;
            case M: code = GLFW_KEY_M; break;
            case N: code = GLFW_KEY_N; break;
            case O: code = GLFW_KEY_O; break;
            case P: code = GLFW_KEY_P; break;
            case Q: code = GLFW_KEY_Q; break;
            case R: code = GLFW_KEY_R; break;
            case S: code = GLFW_KEY_S; break;
            case T: code = GLFW_KEY_T; break;
            case U: code = GLFW_KEY_U; break;
            case V: code = GLFW_KEY_V; break;
            case W: code = GLFW_KEY_W; break;
            case X: code = GLFW_KEY_X; break;
            case Y: code = GLFW_KEY_Y; break;
            case Z: code = GLFW_KEY_Z; break;

            case LEFT_BRACKET: code = GLFW_KEY_LEFT_BRACKET; break;
            case BACKSLASH: code = GLFW_KEY_BACKSLASH; break;
            case RIGHT_BRACKET: code = GLFW_KEY_RIGHT_BRACKET; break;
            case GRAVE_ACCENT: code = GLFW_KEY_GRAVE_ACCENT; break;
            case ESCAPE: code = GLFW_KEY_ESCAPE; break;
            case ENTER: code = GLFW_KEY_ENTER; break;
            case TAB: code = GLFW_KEY_TAB; break;
            case SPACE: code = GLFW_KEY_SPACE; break;
            case BACKSPACE: code = GLFW_KEY_BACKSPACE; break;
            case INSERT: code = GLFW_KEY_INSERT; break;
            case DELETE: code = GLFW_KEY_DELETE; break;
            case RIGHT: code = GLFW_KEY_RIGHT; break;
            case LEFT: code = GLFW_KEY_LEFT; break;
            case DOWN: code = GLFW_KEY_DOWN; break;
            case UP: code = GLFW_KEY_UP; break;
            case PAGE_UP: code = GLFW_KEY_PAGE_UP; break;
            case PAGE_DOWN: code = GLFW_KEY_PAGE_DOWN; break;
            case HOME: code = GLFW_KEY_HOME; break;
            case END: code = GLFW_KEY_END; break;
            case CAPS_LOCK: code = GLFW_KEY_CAPS_LOCK; break;
            case SCROLL_LOCK: code = GLFW_KEY_SCROLL_LOCK; break;
            case NUM_LOCK: code = GLFW_KEY_NUM_LOCK; break;
            case PRINT_SCREEN: code = GLFW_KEY_PRINT_SCREEN; break;
            case PAUSE: code = GLFW_KEY_PAUSE; break;

            case F1: code = GLFW_KEY_F1; break;
            case F2: code = GLFW_KEY_F2; break;
            case F3: code = GLFW_KEY_F3; break;
            case F4: code = GLFW_KEY_F4; break;
            case F5: code = GLFW_KEY_F5; break;
            case F6: code = GLFW_KEY_F6; break;
            case F7: code = GLFW_KEY_F7; break;
            case F8: code = GLFW_KEY_F8; break;
            case F9: code = GLFW_KEY_F9; break;
            case F10: code = GLFW_KEY_F10; break;
            case F11: code = GLFW_KEY_F11; break;
            case F12: code = GLFW_KEY_F12; break;
            case F13: code = GLFW_KEY_F13; break;
            case F14: code = GLFW_KEY_F14; break;
            case F15: code = GLFW_KEY_F15; break;
            case F16: code = GLFW_KEY_F16; break;
            case F17: code = GLFW_KEY_F17; break;
            case F18: code = GLFW_KEY_F18; break;
            case F19: code = GLFW_KEY_F19; break;
            case F20: code = GLFW_KEY_F20; break;
            case F21: code = GLFW_KEY_F21; break;
            case F22: code = GLFW_KEY_F22; break;
            case F23: code = GLFW_KEY_F23; break;
            case F24: code = GLFW_KEY_F24; break;
            case F25: code = GLFW_KEY_F25; break;

            case LEFT_SHIFT: code = GLFW_KEY_LEFT_SHIFT; break;
            case LEFT_CTRL: code = GLFW_KEY_LEFT_CONTROL; break;
            case LEFT_ALT: code = GLFW_KEY_LEFT_ALT; break;
            case LEFT_SUPER: code = GLFW_KEY_LEFT_SUPER; break;
            case RIGHT_SHIFT: code = GLFW_KEY_RIGHT_SHIFT; break;
            case RIGHT_CTRL: code = GLFW_KEY_RIGHT_CONTROL; break;
            case RIGHT_ALT: code = GLFW_KEY_RIGHT_ALT; break;
            case RIGHT_SUPER: code = GLFW_KEY_RIGHT_SUPER; break;
            case MENU: code = GLFW_KEY_MENU; break;
        }
        return code;
    }

}
