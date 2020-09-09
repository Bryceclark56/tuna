package me.bc56.tuna.events.type;

import java.util.UUID;

public class Event {
    public final UUID id = UUID.randomUUID();
    public final UUID source;
    public final String type;

    public Event(UUID source, String type) {
        this.source = source;
        this.type = type;
    }
}
