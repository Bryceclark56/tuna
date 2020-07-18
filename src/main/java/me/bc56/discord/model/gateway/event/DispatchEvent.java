package me.bc56.discord.model.gateway.event;

import com.google.gson.JsonObject;

public class DispatchEvent implements GatewayEvent {
    String eventName;

    JsonObject eventData;

    public DispatchEvent(String eventName, JsonObject eventData) {
        this.eventName = eventName;
        this.eventData = eventData;
    }

    public JsonObject getEventData() {
        return eventData;
    }

    public String getEventName() {
        return eventName;
    }
}
