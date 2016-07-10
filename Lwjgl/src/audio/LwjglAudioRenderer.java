package audio;

import utils.LwjglUtils;
import math.Vector3f;
import org.lwjgl.openal.*;
import utils.Destroyable;

import java.nio.*;
import java.util.*;
import java.util.stream.Collectors;

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

    // handles
    private int handleCount = 0;
    private int[] handles;  // index is al source
    private Map<Integer, Integer> handle2source = new HashMap<>();

    public LwjglAudioRenderer (int maxSources) {
        this.device = ALC10.alcOpenDevice((ByteBuffer) null);
        this.context = ALC10.alcCreateContext(device, (IntBuffer) null);
        this.alcCaps = ALC.createCapabilities(device);
        alcMakeContextCurrent(context);
        this.alCaps = AL.createCapabilities(alcCaps);

        // index is al source handle
        handles = new int[maxSources+1];

        // allocate fixed number of sources
        while (maxSources-- > 0) {
            int src = alGenSources();
            available.add(src);
            handles[src] = 0;
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

    private void checkDestroy () {
        // look for destroyed sound
        List<Sound> toDestroy = buffers.keySet().stream()
                .filter(Sound::isDestroy)
                .collect(Collectors.toCollection(LinkedList::new));

        // delete buffers
        for (Sound sound : toDestroy) {
            int id = buffers.remove(sound);
            alDeleteBuffers(id);
        }
    }

    public void update () {
        //System.out.println("avail="+available.size()+"\tbusy="+busy.size());

        int size = busy.size();
        for (int i = 0; i < size; ++i) {
            int source = busy.poll();

            // if source is stopped, release it
            if (alGetSourcei(source, AL10.AL_SOURCE_STATE) == AL_STOPPED) {
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

        if (source == null) {
            // no available :(
            return -1;
        }

        // get buffer
        Integer buffer = buffers.get(sound);

        if (buffer == null) {
            // create buffer
            createBuffer(sound, buffer = alGenBuffers());
            buffers.put(sound, buffer);
        } else if (sound.isDestroy()) {
            sound.setDirty(false);
            createBuffer(sound, buffer);
        }

        // assign buffer to source & properties
        alSourcei(source, AL_BUFFER, buffer);
        alSourcei(source, AL_LOOPING, loop ? AL_TRUE : AL_FALSE);

        // play source
        alSourcePlay(source);
        busy.add(source);

        // source handle
        //handleCount++;
        handles[source] = ++handleCount;
        handle2source.put(handleCount, source);
        return handleCount;
    }

    @Override
    public void stopSound(int handle) {
        int source = handle2source.get(handle);
        if (handles[source] == handle) {
            alSourceStop(source);
        }
    }

    @Override
    public void pauseSound(int handle) {
        int source = handle2source.get(handle);
        if (handles[source] == handle) {
            alSourcePause(source);
        }
    }

    @Override
    public void resumeSound(int handle) {
        int source = handle2source.get(handle);
        if (handles[source] == handle) {
            alSourcePause(source);
        }
    }

    private void createBuffer (Sound sound, int buffer) {
        int format = LwjglUtils.audioFormat(sound.getFormat());
        Buffer data = sound.getData();
        sound.setDirty(false);

        if (data instanceof ByteBuffer) {
            alBufferData(buffer, format, (ByteBuffer) data, sound.getSampling());
        } else if (data instanceof ShortBuffer) {
            alBufferData(buffer, format, (ShortBuffer) data, sound.getSampling());
        } else if (data instanceof IntBuffer) {
            alBufferData(buffer, format, (IntBuffer) data, sound.getSampling());
        } else if (data instanceof FloatBuffer) {
            alBufferData(buffer, format, (FloatBuffer) data, sound.getSampling());
        }
    }
}
