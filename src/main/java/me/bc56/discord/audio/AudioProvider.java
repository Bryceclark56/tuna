package me.bc56.discord.audio;

public interface AudioProvider {
    public boolean canProvideFrame();
    public byte[] provideFrame();
    public short getFramePos();
}
