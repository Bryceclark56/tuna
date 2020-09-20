package me.bc56.discord.api;

import me.bc56.discord.model.Guild;
import me.bc56.discord.model.Snowflake;
import me.bc56.discord.model.exception.DiscordApiException;
import me.bc56.discord.service.DiscordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;

public class Guilds extends DiscordApiAccessor {
    private static final Logger log = LoggerFactory.getLogger(Guilds.class);

    //TODO: Error throwing

    DiscordService discord;

    public Guilds(DiscordService discordService) {
        super(discordService);
    }

    public Guild get(Snowflake guildId) throws DiscordApiException {
        return this.get(guildId, false);
    }

    public Guild get(Snowflake guildId, boolean withCounts) throws DiscordApiException {
        Call<Guild> request = discord.getGuild(
                String.valueOf(guildId.id),
                withCounts
        );

        return ApiHelper.makeRequest(request);
    }

    public void create() throws DiscordApiException {

    }

    public void delete() throws DiscordApiException {

    }
}
