package me.bc56.discord.model;

import com.google.gson.annotations.SerializedName;

// https://discord.com/developers/docs/resources/voice#voice-state-object
public class VoiceState {
    @SerializedName("guild_id")
    String guildId;

    @SerializedName("channel_id")
    String channelId;

    @SerializedName("user_id")
    String userId;

    GuildMember member;

    @SerializedName("session_id")
    String sessionId;

    Boolean deaf;

    Boolean mute;

    @SerializedName("self_deaf")
    Boolean selfDeaf;

    @SerializedName("self_mute")
    Boolean selfMute;

    @SerializedName("self_stream")
    Boolean selfStream;

    @SerializedName("self_video")
    Boolean selfVideo;

    Boolean suppress;

    public String getGuildId() {
        return guildId;
    }

    public void setGuildId(String guildId) {
        this.guildId = guildId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public GuildMember getMember() {
        return member;
    }

    public void setMember(GuildMember member) {
        this.member = member;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isDeaf() {
        return deaf;
    }

    public void setDeaf(boolean deaf) {
        this.deaf = deaf;
    }

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    public boolean isSelfDeaf() {
        return selfDeaf;
    }

    public void setSelfDeaf(boolean selfDeaf) {
        this.selfDeaf = selfDeaf;
    }

    public boolean isSelfMute() {
        return selfMute;
    }

    public void setSelfMute(boolean selfMute) {
        this.selfMute = selfMute;
    }

    public boolean isSelfStream() {
        return selfStream;
    }

    public void setSelfStream(boolean selfStream) {
        this.selfStream = selfStream;
    }

    public boolean isSelfVideo() {
        return selfVideo;
    }

    public void setSelfVideo(boolean selfVideo) {
        this.selfVideo = selfVideo;
    }

    public boolean isSuppress() {
        return suppress;
    }

    public void setSuppress(boolean suppress) {
        this.suppress = suppress;
    }
}
