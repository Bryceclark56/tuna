package me.bc56.discord.model.voicegateway.payload.data;

import com.google.gson.annotations.SerializedName;
import me.bc56.discord.util.Constants;

public class VoiceHelloPayloadData implements VoiceGatewayPayloadData {
    public transient static final int opCode = Constants.VoiceOpcodes.HELLO;

    @SerializedName("heartbeat_interval")
    private int heartbeatInterval;

    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(int heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }

    @Override
    public int getOpCode() {
        return opCode;
    }
}
