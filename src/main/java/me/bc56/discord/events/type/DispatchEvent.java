package me.bc56.discord.events.type;

import me.bc56.discord.gateway.dispatch.DispatchData;

public class DispatchEvent<E extends DispatchData> extends DiscordEvent {
    public final E data;

    public DispatchEvent(E data) {
        this.data = data;
    }
}
