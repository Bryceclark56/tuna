package me.bc56.tuna.events;

import me.bc56.tuna.events.type.Event;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class EventFilter {
    //TODO: N-time filters (Only match the next N events then remove from filter)
    private final List<UUID> eventSources = new LinkedList<>();
    private final List<String> eventTypes = new LinkedList<>();

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

        if (!eventTypes.isEmpty()) {
            test &= eventTypes.contains(event.type);
        }
        return test;
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
