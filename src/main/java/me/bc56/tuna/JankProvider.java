package me.bc56.tuna;

import me.bc56.discord.audio.AudioProvider;
import me.bc56.discord.audio.AudioTrack;

public class JankProvider implements AudioProvider {
    private AudioTrack track;
    private boolean playing;

    public JankProvider() {
        this.playing = false;
    }

    @Override
    public synchronized boolean canProvideFrame() {
        if (track != null) {
            return track.canProvideFrame() && playing;
        } else {
            return false;
        }
    }

    @Override
    public synchronized byte[] provideFrame() {
        return track.provideFrame();
    }

    @Override
    public synchronized short getFramePos() {
        if (track != null) {
            return track.getFramePos();
        } else {
            return 0;
        }
    }

    public synchronized void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public synchronized void addTrack(AudioTrack track) {
        this.track = track;
    }
}
