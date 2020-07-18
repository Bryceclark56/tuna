package me.bc56.discord;

import me.bc56.discord.model.gateway.event.GatewayEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class EventsManager {
    static final Logger log = LoggerFactory.getLogger(EventsManager.class);

    static Executor runner;

    private static EventsManager instance;

    private final Map<Class<? extends GatewayEvent>, Emitter<? extends GatewayEvent>> emitters;

    public static EventsManager getInstance() {
        if (instance == null) {
            instance = new EventsManager();
        }

        return instance;
    }

    public EventsManager() {
        emitters = new HashMap<>();

        runner = Executors.newWorkStealingPool();
    }

    public <E extends GatewayEvent> void register(Class<E> event, Consumer<E> callback) {
        @SuppressWarnings("unchecked")
        Emitter<E> emitter = (Emitter<E>) emitters.get(event);

        if (emitter == null) {
            emitter = new Emitter<>();
            emitters.put(event, emitter);
        }

        emitter.register(callback);
    }

    //TODO: Should this throw an error on null .get()?
    public <E extends GatewayEvent> Emitter<E> getEmitter(Class<E> event) {
        @SuppressWarnings("unchecked")
        Emitter<E> emitter = (Emitter<E>) emitters.get(event);

        return emitter;
    }

    //TODO: This SHOULD error on null!
    public <E extends GatewayEvent> void emit(E event) {
        @SuppressWarnings("unchecked")
        Class<E> eventClass = (Class<E>) event.getClass();
        @SuppressWarnings("unchecked")
        Emitter<E> emitter = (Emitter<E>) emitters.get(eventClass);

        if (emitter == null) {
            return;
        }

        runner.execute(() -> emitter.emit(event));
    }

    static class Emitter<Event extends GatewayEvent> {
        private final Logger log = LoggerFactory.getLogger(EventsManager.class);

        private final List<Consumer<Event>> listeners;

        public Emitter() {
            listeners = new ArrayList<>();
        }

        public void register(Consumer<Event> listener) {
            listeners.add(listener);
        }

        public void emit(Event event) {
            if (event == null) {
                log.warn("Null event!!");
                return;
            }

            if (!listeners.isEmpty()) {
                for (Consumer<Event> listener : listeners) {
                    listener.accept(event);
                }
            }

            log.debug("Emitting event: " + event.getClass().getSimpleName());
        }
    }
}
