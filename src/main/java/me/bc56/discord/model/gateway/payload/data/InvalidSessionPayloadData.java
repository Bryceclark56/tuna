package me.bc56.discord.model.gateway.payload.data;

import me.bc56.discord.util.Constants;

public class InvalidSessionPayloadData implements GatewayPayloadData {
    public transient static final int opCode = Constants.GatewayPayloadType.INVALID_SESSION;

    @Override
    public int getOpCode() {
        return opCode;
    }
}
