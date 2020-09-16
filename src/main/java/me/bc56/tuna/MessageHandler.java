package me.bc56.tuna;

import me.bc56.discord.DiscordBot;
import me.bc56.discord.model.ChannelMessage;
import me.bc56.tuna.events.*;
import me.bc56.tuna.events.type.Event;
import me.bc56.tuna.events.type.MessageCommandEvent;
import me.bc56.tuna.events.type.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

public class MessageHandler extends TunaModule implements EventReceiver, EventProducer {
    static Logger log = LoggerFactory.getLogger(MessageHandler.class);

    private final LinkedBlockingQueue<ChannelMessage> messages = new LinkedBlockingQueue<>();

    DiscordBot bot;

    String commandDelimiter;

    EventManager eventManager;

    public MessageHandler(EventManager eventManager, DiscordBot bot, String commandDelimiter) {
        this.bot = bot;
        this.commandDelimiter = commandDelimiter;
        this.eventManager = eventManager;

        registerEventReceiver();
    }

    @Override
    public void loop() {
        try {
            handleMessage(messages.take());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleMessage(ChannelMessage message) {
        log.debug("New message!");
        if (isCommand(message)) {
            log.debug("Determined message to be a command, handing over control...");
            MessageCommandEvent event = new MessageCommandEvent(this.moduleId, message);
            eventManager.submitEvent(event);
        }
    }

    public boolean isCommand(ChannelMessage message) {
        return message.getContent().startsWith(commandDelimiter);
    }

    private void registerEventReceiver() {
        EventFilter filter = new EventFilter();
        filter.addEventType(EventConstants.NEW_CHANNEL_MESSAGE);

        eventManager.registerReceiver(this, filter);
    }

    @Override
    public <E extends Event> void enqueue(E event) {
        if (!(event instanceof MessageEvent)) {
            return;
        }

        MessageEvent messageEvent = (MessageEvent) event;

        try {
            messages.put(messageEvent.message);
        } catch (InterruptedException e) {
            log.error("Problem enqueueing MessageEvent", e);
        }
    }
}
