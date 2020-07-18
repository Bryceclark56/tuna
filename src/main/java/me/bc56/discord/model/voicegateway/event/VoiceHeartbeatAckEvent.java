package me.bc56.discord.model.voicegateway.event;

import me.bc56.discord.model.voicegateway.payload.VoiceGatewayPayload;
import me.bc56.discord.model.voicegateway.payload.data.HeartbeatAckVoicePayloadData;

public class VoiceHeartbeatAckEvent implements VoiceGatewayEvent {
    long nonce;

    public VoiceHeartbeatAckEvent(VoiceGatewayPayload payload) {
        HeartbeatAckVoicePayloadData payloadData = (HeartbeatAckVoicePayloadData) payload.getEventData();

        nonce = payloadData.getNonce();
    }

    public long getNonce() {
        return nonce;
    }
}
