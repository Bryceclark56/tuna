package me.bc56.tuna.events.type;

import me.bc56.discord.model.ChannelMessage;
import me.bc56.tuna.events.EventConstants;

import java.util.UUID;

public class MessageEvent extends Event {
    public final ChannelMessage message;

    public MessageEvent(UUID source, ChannelMessage message) {
        super(source, EventConstants.NEW_CHANNEL_MESSAGE);
        this.message = message;
    }
}
