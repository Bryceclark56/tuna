package me.bc56.discord;

import me.bc56.discord.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class EventsManager {
    static final Logger log = LoggerFactory.getLogger(EventsManager.class);

    private static EventsManager instance;

    static Executor runner = Executors.newWorkStealingPool();

    private final Map<Class<? extends Event>, Emitter<? extends Event>> emitters = new HashMap<>();

    public static EventsManager getInstance() {
        if (instance == null) {
            instance = new EventsManager();
        }

        return instance;
    }

    public <E extends Event> void register(Class<E> event, Consumer<E> callback) {
        @SuppressWarnings("unchecked")
        Emitter<E> emitter = (Emitter<E>) emitters.get(event);

        if (emitter == null) {
            emitter = new Emitter<>();
            emitters.put(event, emitter);
        }

        emitter.register(callback);
    }

    //TODO: Should this throw an error on null .get()? Is this even needed?
    public <E extends Event> Emitter<E> getEmitter(Class<E> event) {
        @SuppressWarnings("unchecked")
        Emitter<E> emitter = (Emitter<E>) emitters.get(event);

        return emitter;
    }

    //TODO: This SHOULD error on null!
    public <E extends Event> void emit(E event) {
        @SuppressWarnings("unchecked")
        Class<E> eventClass = (Class<E>) event.getClass();
        String className = eventClass.getSimpleName();
        final String shortClassName = className.substring(0, className.length() - 5);

        @SuppressWarnings("unchecked")
        Emitter<E> emitter = (Emitter<E>) emitters.get(eventClass);

        if (emitter == null) {
            return;
        }

        runner.execute(() -> {
            Thread.currentThread().setName("Event-" + shortClassName + "-" + emitter.numThreads());
            emitter.emit(event);
        });
    }

    static class Emitter<E extends Event> {
        private static final AtomicInteger nThreads = new AtomicInteger();
        private final Logger log;
        private final List<Consumer<E>> listeners = new ArrayList<>();

        public Emitter() {
            log = LoggerFactory.getLogger(this.getClass());
        }

        public void register(Consumer<E> listener) {
            listeners.add(listener);
        }

        public void emit(E event) {
            nThreads.incrementAndGet();

            if (event == null) {
                log.warn("Null event!!");
                return;
            }

            log.debug("Emitting event: " + event.getClass().getSimpleName());
            if (!listeners.isEmpty()) {
                for (Consumer<E> listener : listeners) {
                    listener.accept(event);
                }
            }

            nThreads.decrementAndGet();
        }

        public int numThreads() {
            return nThreads.get();
        }
    }
}
