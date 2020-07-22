package me.bc56.tuna;

import me.bc56.discord.audio.AudioProvider;
import me.bc56.discord.audio.AudioTrack;

public class JankProvider implements AudioProvider {
    private AudioTrack track;
    private boolean playing = false;

    public JankProvider(AudioTrack track) {
        this.track = track;
    }

    @Override
    public synchronized boolean canProvideFrame() {
        return track.canProvideFrame() && playing;
    }

    @Override
    public synchronized byte[] provideFrame() {
        return track.provideFrame();
    }

    @Override
    public synchronized short getFramePos() {
        return track.getFramePos();
    }

    public synchronized void setPlaying(boolean playing) {
        this.playing = playing;
    }
}
