package me.bc56.discord.model.gateway.payload;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import me.bc56.discord.adapter.GatewayPayloadAdapter;
import me.bc56.discord.model.gateway.payload.data.GatewayPayloadData;

@JsonAdapter(GatewayPayloadAdapter.class)
public class GatewayPayload {
    @SerializedName("op")
    int opCode;

    @SerializedName("t")
    String eventName;

    @SerializedName("d")
    GatewayPayloadData eventData;

    @SerializedName("s")
    Integer sequence;

    public int getOpCode() {
        return opCode;
    }

    public void setOpCode(int opCode) {
        this.opCode = opCode;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public GatewayPayloadData getEventData() {
        return eventData;
    }

    public void setEventData(GatewayPayloadData eventData) {
        this.eventData = eventData;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }
}
