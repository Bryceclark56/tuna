package me.bc56.tuna;

import me.bc56.discord.audio.AudioProvider;
import me.bc56.discord.audio.AudioTrack;

public class JankProvider implements AudioProvider {
    AudioTrack track;

    public JankProvider(AudioTrack track) {
        this.track = track;
    }
    @Override
    public boolean canProvideFrame() {
        return track.canProvideFrame();
    }

    @Override
    public byte[] provideFrame() {
        return track.provideFrame();
    }

    @Override
    public short getFramePos() {
        return track.getFramePos();
    }
}
