package me.bc56.generic.event;


public abstract class EventDispatcher {
    public abstract void registerSink(Class<? extends Event> eventType, EventSink sink);
    public abstract void dispatch(Event event);
}
