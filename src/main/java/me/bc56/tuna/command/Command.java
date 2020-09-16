package me.bc56.tuna.command;

import me.bc56.discord.DiscordBot;
import me.bc56.tuna.model.CommandCircumstances;
import me.bc56.tuna.model.ParsedCommandString;

public abstract class Command {
    public abstract void run(DiscordBot bot, CommandCircumstances information);
}
