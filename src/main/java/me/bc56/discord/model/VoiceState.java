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

    boolean deaf;

    boolean mute;

    @SerializedName("self_deaf")
    boolean selfDeaf;

    @SerializedName("self_mute")
    boolean selfMute;

    @SerializedName("self_stream")
    boolean selfStream;

    @SerializedName("self_video")
    boolean selfVideo;

    boolean suppress;
}
