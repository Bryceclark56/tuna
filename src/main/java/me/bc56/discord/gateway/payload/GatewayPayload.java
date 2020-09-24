package me.bc56.discord.gateway.payload;

import me.bc56.discord.Constants;
import me.bc56.discord.gateway.payload.type.GatewayPayloadType;

public class GatewayPayload<T extends GatewayPayloadType> {
    public final Constants.GatewayOpcode opCode;
    public final T data;
    public final String name;
    public final Integer sequence;

    public GatewayPayload(Constants.GatewayOpcode opCode, T data, String name, Integer sequence) {
        this.opCode = opCode;
        this.data = data;
        this.name = name;
        this.sequence = sequence;
    }
}
