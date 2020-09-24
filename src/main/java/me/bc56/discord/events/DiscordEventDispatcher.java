package me.bc56.discord.events;

import me.bc56.generic.event.Event;
import me.bc56.generic.event.EventDispatcher;
import me.bc56.generic.event.EventSink;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DiscordEventDispatcher extends EventDispatcher {
    Map<Class<? extends Event>, Set<EventSink>> eventToSinks;

    ExecutorService threadPool = Executors.newWorkStealingPool();

    public DiscordEventDispatcher() {
        Map<Class<? extends Event>, Set<EventSink>> map = new HashMap<>();

        eventToSinks = Collections.synchronizedMap(map);
    }

    @Override
    public void registerSink(Class<? extends Event> eventType, EventSink sink) {
        Set<EventSink> sinks;

        if (!eventToSinks.containsKey(eventType)) {
            sinks = new TreeSet<>();
            eventToSinks.put(eventType, Collections.synchronizedSet(sinks));
        }
        else {
            sinks = eventToSinks.get(eventType);
        }


        sinks.add(sink);
    }

    @Override
    public void dispatch(Event event) {
        threadPool.submit(() -> {
            var sinks = eventToSinks.get(event.getType());

            sinks.forEach((sink) -> sink.send(event));
        });
    }
}
