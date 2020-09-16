package me.bc56.tuna.model;

import me.bc56.discord.model.ChannelMessage;

public class ParsedCommandString {
    public final String commandName;
    public final String[] arguments;

    public ParsedCommandString(String name, String[] args) {
        commandName = name;
        arguments = args;
    }
}
