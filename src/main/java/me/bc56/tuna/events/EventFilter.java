package me.bc56.tuna.events;

import me.bc56.tuna.events.type.Event;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class EventFilter {
    //TODO: N-time filters (Only match the next N events then remove from filter)
    private final Set<UUID> eventSources = new HashSet<>();
    private final Set<String> eventTypes = new HashSet<>();

    /**
     * @param event event to check against filters
     * @return      true if event matches filters, false otherwise
     */
    public boolean checkEvent(Event event) {
        boolean test;

        if (eventSources.isEmpty()) {
            test = true;
        }
        else {
            test = eventSources.contains(event.source);
        }

        if (test && !eventTypes.isEmpty()) {
            return eventTypes.contains(event.type);
        }

        return false;
    }

    public void addEventSource(UUID eventSource) {
        eventSources.add(eventSource);
    }

    public void removeEventSource(UUID eventSource) {
        eventSources.remove(eventSource);
    }

    public void addEventType(String eventType) {
        eventTypes.add(eventType);
    }

    public void removeEventType(String eventType) {
        eventTypes.remove(eventType);
    }
}
