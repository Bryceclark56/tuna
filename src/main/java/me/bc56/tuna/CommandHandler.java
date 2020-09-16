package me.bc56.tuna;

import me.bc56.discord.DiscordBot;
import me.bc56.discord.model.ChannelMessage;
import me.bc56.tuna.command.CommandRegistry;
import me.bc56.tuna.events.EventConstants;
import me.bc56.tuna.events.EventFilter;
import me.bc56.tuna.events.EventManager;
import me.bc56.tuna.events.EventReceiver;
import me.bc56.tuna.events.type.Event;
import me.bc56.tuna.events.type.MessageCommandEvent;
import me.bc56.tuna.model.CommandCircumstances;
import me.bc56.tuna.model.ParsedCommandString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

public class CommandHandler extends TunaModule implements EventReceiver {
    private static final Logger log = LoggerFactory.getLogger(CommandHandler.class);

    LinkedBlockingQueue<MessageCommandEvent> rawCommandEvents = new LinkedBlockingQueue<>();
    //LinkedBlockingQueue<CommandCircumstances> parsedCommands = new LinkedBlockingQueue<>();

    EventManager eventManager;
    DiscordBot bot;

    public CommandHandler(EventManager eventManager, DiscordBot bot) {
        this.eventManager = eventManager;
        this.bot = bot;

        registerEventReceiver();
    }

    @Override
    public void loop() {
        try {
            handleCommand(rawCommandEvents.take());
        } catch (InterruptedException e) {
            log.error("Error while getting command event", e);
        }
    }

    private void handleCommand(MessageCommandEvent event) {
        var parsedCommandString = parseCommand(event.message.getContent());

        var commandCircumstances = new CommandCircumstances(parsedCommandString, event.message);
        CommandRegistry.runCommand(bot, commandCircumstances);
    }

    @Override
    public <E extends Event> void enqueue(E event) {
        if (!(event instanceof MessageCommandEvent)) {
            return;
        }

        MessageCommandEvent commandEvent = (MessageCommandEvent) event;

        try {
            rawCommandEvents.put(commandEvent);
        } catch (InterruptedException e) {
            log.error("Unable to queue new command", e);
        }
    }

    public ParsedCommandString parseCommand(String commandString) {
        String[] tokens = commandString.substring(1).split(" ");

        return new ParsedCommandString(
                tokens[0],
                Arrays.copyOfRange(tokens, 1, tokens.length)
        );
    }

    private void registerEventReceiver() {
        EventFilter filter = new EventFilter();
        filter.addEventType(EventConstants.NEW_MESSAGE_COMMAND);

        eventManager.registerReceiver(this, filter);
    }
}
