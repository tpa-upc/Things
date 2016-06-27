package audio;

import graphics.LwjglUtils;
import math.Vector3f;
import org.lwjgl.openal.*;
import utils.Destroyable;

import java.nio.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;

/**
 * Created by germangb on 19/06/16.
 */
public class LwjglAudioRenderer implements AudioRenderer, Destroyable {

    long device;
    long context;

    ALCCapabilities alcCaps;
    ALCapabilities alCaps;

    // fixed number of sources
    LinkedList<Integer> available = new LinkedList<>();
    LinkedList<Integer> busy = new LinkedList<>();
    AudioStats stats = new AudioStats();

    // buffers
    Map<Sound, Integer> buffers = new HashMap<>();

    public LwjglAudioRenderer (int maxSources) {
        this.device = ALC10.alcOpenDevice((ByteBuffer) null);
        this.context = ALC10.alcCreateContext(device, (IntBuffer) null);
        this.alcCaps = ALC.createCapabilities(device);
        alcMakeContextCurrent(context);
        this.alCaps = AL.createCapabilities(alcCaps);

        // allocate fixed number of sources
        while (maxSources-- > 0) {
            available.add(alGenSources());
        }
    }

    @Override
    public void destroy () {
        // free sources & buffers
        busy.forEach(AL10::alDeleteSources);
        available.forEach(AL10::alDeleteSources);
        buffers.values().forEach(AL10::alDeleteBuffers);

        // destroy audio context & close device
        ALC10.alcDestroyContext(context);
        ALC10.alcCloseDevice(device);
    }

    public void update () {
        int size = busy.size();
        for (int i = 0; i < size; ++i) {
            int source = busy.poll();

            // if source is stopped, release it
            if (alGetSourcei(source, AL10.AL_STOPPED) == AL_TRUE) {
                available.add(source);
            } else {
                busy.add(source);
            }
        }
    }

    @Override
    public AudioStats getStats() {
        stats.buffers = buffers.size();
        stats.playing = busy.size();
        return stats;
    }

    @Override
    public void setListener(Vector3f pos, Vector3f look, Vector3f up) {
        alListener3f(AL_POSITION, pos.x, pos.y, pos.z);
        alListenerfv(AL_ORIENTATION, new float[] {
            //pos.x, pos.y, pos.z,

            look.x, look.y, look.z,
            up.x, up.y, up.z
        });
    }

    @Override
    public int playSound(Sound sound, boolean loop) {
        // Get a source
        Integer source = available.poll();

        if (sound == null) {
            // no available :(
            return -1;
        }

        // get buffer
        Integer buffer = buffers.get(sound);

        if (buffer == null) {
            // create buffer
            buffer = createBuffer(sound);
            buffers.put(sound, buffer);
        }

        // assign buffer to source & properties
        alSourcei(source, AL_BUFFER, buffer);
        alSourcei(source, AL_LOOPING, loop ? AL_TRUE : AL_FALSE);

        // play source
        alSourcePlay(source);
        busy.add(source);

        // return al source as a handle
        return source;
    }

    @Override
    public void stopSound(int handle) {
        alSourceStop(handle);
    }

    @Override
    public void pauseSound(int handle) {
        alSourcePause(handle);
    }

    @Override
    public void resumeSound(int handle) {
        alSourcePlay(handle);
    }

    private Integer createBuffer (Sound sound) {
        int format = LwjglUtils.audioFormat(sound.getFormat());
        Buffer data = sound.getData();

        int buffer = alGenBuffers();

        if (data instanceof ByteBuffer) {
            alBufferData(buffer, format, (ByteBuffer) data, sound.getSampling());
        } else if (data instanceof ShortBuffer) {
            alBufferData(buffer, format, (ShortBuffer) data, sound.getSampling());
        } else if (data instanceof IntBuffer) {
            alBufferData(buffer, format, (ByteBuffer) data, sound.getSampling());
        } else if (data instanceof FloatBuffer) {
            alBufferData(buffer, format, (FloatBuffer) data, sound.getSampling());
        }

        return buffer;
    }
}
