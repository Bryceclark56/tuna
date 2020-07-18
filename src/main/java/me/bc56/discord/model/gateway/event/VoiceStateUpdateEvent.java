package me.bc56.discord.model.gateway.event;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.bc56.discord.model.gateway.payload.GatewayPayload;
import me.bc56.discord.model.gateway.payload.data.VoiceStateUpdatePayloadData;

public class VoiceStateUpdateEvent implements GatewayEvent {

    String guildId;

    String channelId;

    boolean selfMute;

    boolean selfDeaf;

    public VoiceStateUpdateEvent(GatewayPayload payload) {
        VoiceStateUpdatePayloadData payloadData = (VoiceStateUpdatePayloadData) payload.getEventData();

        guildId = payloadData.getGuildId();
        channelId = payloadData.getChannelId();
        selfMute = payloadData.isSelfMute();
        selfDeaf = payloadData.isSelfDeaf();
    }

    public String getGuildId() {
        return guildId;
    }

    public String getChannelId() {
        return channelId;
    }

    public boolean isSelfMute() {
        return selfMute;
    }

    public boolean isSelfDeaf() {
        return selfDeaf;
    }
}
