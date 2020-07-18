package me.bc56.discord.model.gateway.payload.data;

import me.bc56.discord.util.Constants;

public class HeartbeatAckPayloadData implements GatewayPayloadData {
    public transient static final int opCode = Constants.GatewayPayloadType.HEARTBEAT_ACK;

    @Override
    public int getOpCode() {
        return opCode;
    }
}
