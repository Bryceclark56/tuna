package me.bc56.tuna.events;

import me.bc56.tuna.events.type.Event;

public interface EventReceiver {
    <E extends Event> void enqueue(E event);
}
