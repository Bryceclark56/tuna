package me.bc56.discord.model.voicegateway.event;

import com.google.gson.Gson;
import me.bc56.discord.model.voicegateway.payload.VoiceGatewayPayload;
import me.bc56.discord.model.voicegateway.payload.data.VoiceHelloPayloadData;

public class VoiceHelloEvent implements VoiceGatewayEvent {
    private int heartbeatInterval;

    public VoiceHelloEvent(VoiceGatewayPayload payload) {
        Gson gson = new Gson();

        VoiceHelloPayloadData voiceHello = (VoiceHelloPayloadData) payload.getEventData();

        heartbeatInterval = voiceHello.getHeartbeatInterval();
    }

    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }
}
