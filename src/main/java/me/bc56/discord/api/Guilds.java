package me.bc56.discord.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.bc56.discord.model.Guild;
import me.bc56.discord.model.Snowflake;
import me.bc56.discord.model.exception.DiscordApiException;
import me.bc56.discord.service.DiscordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

public class Guilds extends DiscordApiAccessor {
    private static final Logger log = LoggerFactory.getLogger(Guilds.class);

    //TODO: Error throwing

    DiscordService discord;

    public Guilds(DiscordService discordService) {
        super(discordService);
    }

    public Guild get(Snowflake guildId) throws DiscordApiException {
        Call<Guild> request = discord.getGuild(
                String.valueOf(guildId.id),
                false
        );

        try {
            Response<Guild> response = request.execute();

            if (response.isSuccessful()) {
                return response.body();
            }

            // Bad response if reached
            throw ApiHelper.getException(response);
        } catch (IOException e) {
            log.error("Exception while sending HTTP request to discord/getGuild", e);
            //TODO: Proper handling of this situation
            return null;
        }
    }

    public void getWithCounts(Snowflake guildId) throws DiscordApiException {

    }

    public void create() throws DiscordApiException {

    }

    public void delete() {

    }
}
