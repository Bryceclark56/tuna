package me.bc56.discord.audio;

import club.minnced.opus.util.OpusLibrary;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.jna.ptr.PointerByReference;
import org.eclipse.collections.api.list.primitive.ByteList;
import org.eclipse.collections.api.list.primitive.MutableByteList;
import org.eclipse.collections.api.list.primitive.MutableShortList;
import org.eclipse.collections.impl.factory.primitive.ByteLists;
import org.eclipse.collections.impl.factory.primitive.ShortLists;
import tomp2p.opuswrapper.Opus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

public class AudioTrack {
    private static final int OPUS_FRAME_MAX_BYTES = 1275;
    private static final int OPUS_FRAME_SIZE = 960;
    private static final int OPUS_FRAME_TIME = 20; // 60 ms

    private static final int OPUS_SAMPLE_RATE = 48000; // 48 khz
    private static final int OPUS_CHANNEL_COUNT = 2; // stereo audio

    private String name;
    private List<ByteList> opusFrames;

    private short framePos = 0;

    public static class Builder {
        private String name;
        private List<ByteList> opusFrames;

        public Builder(String name) {
            this.name = name;

            opusFrames = new ArrayList<>();
        }

        public Builder addByteFormattedShortPCM(ByteList pcm) {
            MutableShortList shorts = ShortLists.mutable.empty();

            // Convert bytes to shorts
            for (int i = 0; i < pcm.size(); i += 2) {
                byte b0 = pcm.get(i);
                byte b1 = pcm.get(i + 1);

                // TODO; This might be the wrong way around - verify!
                short s = (short) ((((short) b1) << 8) | ((short) b0));
                shorts.add(s);
            }

            return addShortPCM(shorts);
        }

        public Builder addShortPCM(MutableShortList pcm) {
            // Pad out data
            int remainder = (OPUS_FRAME_SIZE * OPUS_CHANNEL_COUNT) - pcm.size() % (OPUS_FRAME_SIZE * OPUS_CHANNEL_COUNT);
            for (int i = 0; i < remainder; i++) {
                pcm.add((short) 0);
            }

            //System.setProperty("jna.nosys", "true");
            //System.setProperty("jna.debug_load", "true");
            try {
                OpusLibrary.loadFromJar();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Opus instance = Opus.INSTANCE;

            // Create opus encoder
            IntBuffer errBuf = IntBuffer.allocate(8); // Yeah, we don't actually use this...
            PointerByReference opusEncoder = instance.opus_encoder_create(OPUS_SAMPLE_RATE, OPUS_CHANNEL_COUNT, Opus.OPUS_APPLICATION_AUDIO, errBuf);

            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            int frameCount = pcm.size() / (OPUS_FRAME_SIZE * OPUS_CHANNEL_COUNT);
            ShortBuffer pcmBuffer = ShortBuffer.allocate(OPUS_FRAME_SIZE * OPUS_CHANNEL_COUNT);
            ByteBuffer frameBuffer = ByteBuffer.allocate(OPUS_FRAME_MAX_BYTES);
            for (int i = 0; i < 1000; i++) { // TODO: Fix segfaults
                for (int j = i * (OPUS_FRAME_SIZE * OPUS_CHANNEL_COUNT); j < (i + 1) * (OPUS_FRAME_SIZE * OPUS_CHANNEL_COUNT); j++) {
                    pcmBuffer.put(pcm.get(j));
                }

                // Encode an opus frame
                int frameLength = instance.opus_encode(opusEncoder, pcmBuffer, 2880, frameBuffer, OPUS_FRAME_MAX_BYTES);

                byte[] filled = new byte[frameLength];
                System.arraycopy(frameBuffer.array(), 0, filled, 0, frameLength);

                opusFrames.add(ByteLists.immutable.of(filled));

                pcmBuffer.clear();
                frameBuffer.clear();
            }

            // Destroy opus encoder
            instance.opus_encoder_destroy(opusEncoder);

            return this;
        }

        public Builder addOpusJSON() {
            // TODO; Don't hardcode this!
            try {
                File json = new File("test.json");
                BufferedReader reader = new BufferedReader(new FileReader(json));

                Gson gson = new Gson();
                JsonObject whatever = gson.fromJson(reader, JsonObject.class);
                JsonArray frames = whatever.getAsJsonArray("frames");

                frames.forEach(frame -> {
                    JsonArray data = frame.getAsJsonObject().getAsJsonArray("data");

                    MutableByteList byteList = ByteLists.mutable.empty();
                    data.forEach(b -> {
                        byteList.add((byte) b.getAsInt());
                    });
                    opusFrames.add(byteList);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            return this;
        }

        public AudioTrack build() {
            AudioTrack audioTrack = new AudioTrack();
            audioTrack.name = this.name;
            audioTrack.opusFrames = this.opusFrames;
            audioTrack.framePos = 0;

            return audioTrack;
        }
    }

    public AudioTrack() {};

    public boolean canProvideFrame() {
        return framePos < (opusFrames.size() - 1);
    }

    public byte[] provideFrame() {
        return opusFrames.get(framePos++).toArray();
    }

    public short getFramePos() {
        return framePos;
    }
}
