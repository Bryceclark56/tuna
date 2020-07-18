package me.bc56.discord.model.gateway.event;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.bc56.discord.model.ChannelMessage;

public class MessageCreateEvent extends DispatchEvent {

    private ChannelMessage message;

    public MessageCreateEvent(String eventName, JsonObject eventData) {
        super(eventName, eventData);

        Gson gson = new Gson();
        message = gson.fromJson(eventData, ChannelMessage.class);
    }

    public MessageCreateEvent(DispatchEvent dispatchEvent) {
        this(dispatchEvent.getEventName(), dispatchEvent.getEventData());
    }

    public ChannelMessage getMessage() {
        return message;
    }
}
