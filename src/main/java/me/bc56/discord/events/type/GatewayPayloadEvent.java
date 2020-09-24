package me.bc56.discord.events.type;

import me.bc56.discord.gateway.payload.GatewayPayload;
import me.bc56.discord.gateway.payload.type.GatewayPayloadType;
import me.bc56.generic.event.Event;

public class GatewayPayloadEvent<E extends GatewayPayloadType> extends DiscordEvent {
    public final GatewayPayload<E> payload;

    public GatewayPayloadEvent(GatewayPayload<E> payload) {
        this.payload = payload;
    }
}
