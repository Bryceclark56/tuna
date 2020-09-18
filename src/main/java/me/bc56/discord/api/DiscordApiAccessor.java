package me.bc56.discord.api;

import me.bc56.discord.service.DiscordService;

public abstract class DiscordApiAccessor {

    DiscordService discord;

    public DiscordApiAccessor(DiscordService discordService) {
        discord = discordService;
    }
}
