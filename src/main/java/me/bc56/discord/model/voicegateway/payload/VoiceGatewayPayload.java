package me.bc56.discord.model.voicegateway.payload;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import me.bc56.discord.adapter.VoiceGatewayPayloadAdapter;
import me.bc56.discord.model.voicegateway.payload.data.VoiceGatewayPayloadData;

@JsonAdapter(VoiceGatewayPayloadAdapter.class)
public class VoiceGatewayPayload {
    @SerializedName("op")
    int opCode;

    @SerializedName("d")
    VoiceGatewayPayloadData eventData;

    public int getOpCode() {
        return opCode;
    }

    public void setOpCode(int opCode) {
        this.opCode = opCode;
    }

    public VoiceGatewayPayloadData getEventData() {
        return eventData;
    }

    public void setEventData(VoiceGatewayPayloadData eventData) {
        this.eventData = eventData;
    }
}
