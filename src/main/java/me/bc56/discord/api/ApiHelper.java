package me.bc56.discord.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.bc56.discord.model.exception.DiscordApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;

import java.io.IOException;
import java.util.Objects;

public class ApiHelper {
    private static Logger log = LoggerFactory.getLogger(ApiHelper.class);

    public static DiscordApiException getException(Response<?> requestResponse) {
        Objects.requireNonNull(requestResponse.errorBody(),
                "Request has a null error body. Are you sure this is an unsuccessful request?");

        JsonObject jsonError;
        try {
            jsonError = (new Gson()).fromJson(requestResponse.errorBody().string(), JsonObject.class);
        } catch (IOException e) {
            log.error("Problem parsing JSON body of HTTP response", e);
            throw new RuntimeException("How did it fail to turn it into a string?!");
        }

        int jsonCode = jsonError.get("code").getAsInt();
        String jsonMessage = jsonError.get("message").getAsString();

        return new DiscordApiException(requestResponse.code(), jsonCode, jsonMessage);
    }
}
