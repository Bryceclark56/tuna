package me.bc56.tuna.events.type;

import me.bc56.discord.model.ChannelMessage;
import me.bc56.tuna.events.EventConstants;

import java.util.UUID;

public class MessageCommandEvent extends Event{
    public final ChannelMessage message;

    public MessageCommandEvent(UUID source, ChannelMessage message) {
        super(source, EventConstants.NEW_MESSAGE_COMMAND);

        this.message = message;
    }
}
