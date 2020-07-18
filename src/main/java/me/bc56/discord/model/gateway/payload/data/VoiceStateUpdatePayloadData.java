package me.bc56.discord.model.gateway.payload.data;

import com.google.gson.annotations.SerializedName;
import me.bc56.discord.model.GuildMember;
import me.bc56.discord.util.Constants;

public class VoiceStateUpdatePayloadData implements GatewayPayloadData {
    public transient static final int opCode = Constants.GatewayPayloadType.VOICE_STATE_UPDATE;

    @SerializedName("guild_id")
    private String guildId;

    @SerializedName("channel_id")
    private String channelId;

    @SerializedName("user_id")
    String userId;

    GuildMember member;



    @SerializedName("self_mute")
    boolean selfMute;

    @SerializedName("self_deaf")
    boolean selfDeaf;

    @Override
    public int getOpCode() {
        return opCode;
    }

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

    public boolean isSelfMute() {
        return selfMute;
    }

    public void setSelfMute(boolean selfMute) {
        this.selfMute = selfMute;
    }

    public boolean isSelfDeaf() {
        return selfDeaf;
    }

    public void setSelfDeaf(boolean selfDeaf) {
        this.selfDeaf = selfDeaf;
    }
}
