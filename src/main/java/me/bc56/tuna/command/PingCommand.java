package me.bc56.tuna.command;

import me.bc56.discord.DiscordBot;
import me.bc56.tuna.model.CommandCircumstances;

public class PingCommand extends Command {
    @Override
    public void run(DiscordBot bot, CommandCircumstances information) {
        var channel = information.origin.getChannelId();

        bot.sendMessage(channel, "Pong!");
    }
}
