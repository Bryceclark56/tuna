package me.bc56.discord.gateway.payload.type;

import me.bc56.discord.gateway.dispatch.type.DispatchData;

public class Dispatch<E extends DispatchData> implements GatewayPayloadType {
    public final E data;

    public Dispatch(E data) {
        this.data = data;
    }
}
