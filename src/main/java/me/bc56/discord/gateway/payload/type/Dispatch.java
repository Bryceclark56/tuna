package me.bc56.discord.gateway.payload.type;

import me.bc56.discord.Discord;
import me.bc56.discord.gateway.dispatch.DispatchData;

public class Dispatch<E extends DispatchData> implements GatewayPayloadType {
    E data;

    public Dispatch(E data) {
        this.data = data;
    }
}
