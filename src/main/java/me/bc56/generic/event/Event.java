package me.bc56.generic.event;


import java.util.UUID;

public abstract class Event {
    public final UUID id = UUID.randomUUID();

    public Class<? extends Event> getType() { return getClass(); }
}
