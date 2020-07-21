package me.bc56.discord.model.voicegateway.event;

import com.google.gson.annotations.SerializedName;
import me.bc56.discord.model.voicegateway.payload.VoiceGatewayPayload;
import me.bc56.discord.model.voicegateway.payload.data.VoiceSessionDescriptionPayloadData;

public class VoiceSessionDescriptionEvent implements VoiceGatewayEvent {
    private final String mode;

    @SerializedName("secret_key")
    private final Byte[] secretKey;

    public VoiceSessionDescriptionEvent(VoiceGatewayPayload payload) {
        VoiceSessionDescriptionPayloadData payloadData = (VoiceSessionDescriptionPayloadData) payload.getEventData();

        mode = payloadData.getMode();
        secretKey = payloadData.getSecretKey();
    }

    public String getMode() {
        return mode;
    }

    public Byte[] getSecretKey() {
        return secretKey;
    }
}
