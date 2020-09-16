package me.bc56.tuna.model;

import me.bc56.discord.model.ChannelMessage;

public class CommandCircumstances {
    public final ParsedCommandString parsedCommand;
    public final ChannelMessage origin;

    public CommandCircumstances(ParsedCommandString parsedCommand, ChannelMessage origin) {
        this.parsedCommand = parsedCommand;
        this.origin = origin;
    }
}
