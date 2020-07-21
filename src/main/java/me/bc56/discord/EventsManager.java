package me.bc56.discord;

import me.bc56.discord.model.Event;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class EventsManager {
    static final Logger log = LoggerFactory.getLogger(EventsManager.class);
    private static EventsManager instance;

    static Executor runner = Executors.newWorkStealingPool();
    private final AtomicInteger numThreads = new AtomicInteger();

    private final Map<Class<? extends Event>, Emitter<? extends Event>> emitters = new HashMap<>();

    public static EventsManager getInstance() {
        if (instance == null) {
            instance = new EventsManager();
        }

        return instance;
    }

    public <E extends Event> CompletableFuture<E> register(Class<E> event, @Nullable Consumer<E> callback) {
        @SuppressWarnings("unchecked")
        Emitter<E> emitter = (Emitter<E>) emitters.get(event);

        if (emitter == null) {
            emitter = new Emitter<>();
            emitters.put(event, emitter);
        }

        return emitter.register(callback);
    }

    public <E extends Event> CompletableFuture<E> register(Class<E> event) {
        return this.register(event, null);
    }

    // Blocks and waits for event to be fired
    public <E extends Event> void waitFor(Class<E> event) {
        try {
            this.register(event).get();
        } catch (InterruptedException | ExecutionException e) {
            //TODO: Should we worry about these exceptions?
            log.error("Problem while waiting for event", e);
        }
    }

    /* // Blocks until events are all emitted
    @SafeVarargs
    public List<? extends Event> waitForAll(Class<? extends Event>... events) {
        List<? extends Event> emittedEvents = new ArrayList<>();

        //TODO: Is this something to worry about??
        @SuppressWarnings("unchecked")
        CompletableFuture<? extends Event>[] futures = new CompletableFuture[events.length];
        int i = 0;

        for (Class<? extends Event> event : events) {
            CompletableFuture<? extends Event> eventFuture = getEventFuture(event);

            futures[i++] = eventFuture;
        }

        try {
            CompletableFuture.allOf(futures).get();

            for (i = 0; i < events.length; ++i) {
                emittedEvents.add(futures[i].get());
            }
        } catch (InterruptedException | ExecutionException e) {
            //TODO: Should we worry about these exceptions?
            log.error("Problem while waiting for events", e);
        }
    } */

    public <E extends Event> CompletableFuture<E> getEventFuture(Class<E> event) {
        CompletableFuture<E> eventFuture = new CompletableFuture<>();

        this.register(event, eventFuture::complete);

        return eventFuture;
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
            Thread.currentThread().setName("Event-" + numThreads.getAndIncrement() + "-" + shortClassName);
            emitter.emit(event);
            numThreads.decrementAndGet();
        });
    }

    static class Emitter<E extends Event> {
        private final Logger log;
        private final List<Consumer<E>> callbacks = new ArrayList<>();
        private final List<CompletableFuture<E>> futures = new ArrayList<>();

        public Emitter() {
            log = LoggerFactory.getLogger(this.getClass());
        }

        public CompletableFuture<E> register(@Nullable Consumer<E> callback) {
            if (callback != null) {
                callbacks.add(callback);
            }

            var future = new CompletableFuture<E>();

            futures.add(future);

            return future;
        }

        public void emit(E event) {
            if (event == null) {
                //TODO: Should this throw an exception?
                log.warn("Null event!!");
                return;
            }

            log.debug("Emitting event: " + event.getClass().getSimpleName());
            if (!callbacks.isEmpty()) {
                for (Consumer<E> listener : callbacks) {
                    if (listener != null) {
                        listener.accept(event);
                    }
                }
            }

            if (!futures.isEmpty()) {
                for(CompletableFuture<E> future : futures) {
                    future.complete(event);
                }
            }
        }
    }
}
