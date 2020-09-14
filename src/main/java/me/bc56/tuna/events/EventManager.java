package me.bc56.tuna.events;

import me.bc56.tuna.TunaModule;
import me.bc56.tuna.events.type.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;

public class EventManager extends TunaModule {
    private static Logger log = LoggerFactory.getLogger(EventManager.class);
    private static EventManager instance;

    private final SynchronousQueue<Event> queuedEvents = new SynchronousQueue<>();
    //TODO: Allow multiple filters
    private final Map<EventReceiver, EventFilter> eventReceivers = new HashMap<>();

    private boolean isRunning = false;

    public synchronized static EventManager getInstance() {
        if (instance == null) {
            instance = new EventManager();
        }

        return instance;
    }

    @Override
    protected void loop() {
        while (isRunning()) {
            Event event;
            try {
                event = queuedEvents.take();
            } catch (InterruptedException e) {
                log.error("EventManager interrupted while waiting for event", e);
                continue;
            }

            eventReceivers.forEach((receiver, filter) -> {
                if (filter.checkEvent(event)) {
                    receiver.enqueue(event);
                }
            });
        }
    }

    public void submitEvent(Event event) {
        try {
            queuedEvents.put(event);
        } catch (InterruptedException e) {
            e.printStackTrace();
            //TODO: Print event information?
            log.error("Thread interrupted while waiting for event to be consumed", e);
        }
    }

    public void registerReceiver(EventReceiver receiver, EventFilter filter) {
        eventReceivers.put(receiver, filter);
    }
}
