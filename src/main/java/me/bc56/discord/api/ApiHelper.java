package me.bc56.discord.api;

import checkers.nullness.quals.NonNull;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.bc56.discord.model.exception.DiscordApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;

public class ApiHelper {
    private static Logger log = LoggerFactory.getLogger(ApiHelper.class);

    //Creates and returns a DiscordAPIException for a failed request to the Discord API
    public static DiscordApiException getException(@NonNull Response<?> requestResponse) {
        Objects.requireNonNull(requestResponse.errorBody(),
                "Request has a null error body. Are you sure this is an unsuccessful request?");

        JsonObject jsonError;
        try {
            jsonError = (new Gson()).fromJson(requestResponse.errorBody().string(), JsonObject.class);
        } catch (IOException e) {
            log.error("Problem parsing JSON body of HTTP response", e);
            throw new UncheckedIOException(e);
        }

        int jsonCode = jsonError.get("code").getAsInt();
        String jsonMessage = jsonError.get("message").getAsString();

        return new DiscordApiException(requestResponse.code(), jsonCode, jsonMessage);
    }

    public static <E> E makeRequest(@NonNull Call<E> request) throws DiscordApiException {
        try {
            Response<E> response = request.execute();

            if (response.isSuccessful()) {
                return response.body();
            }
            else {
                // Bad response if reached
                throw getException(response);
            }
        } catch (IOException e) {
            log.error("Problem while sending HTTP request to {}", request.request().url(), e);
            throw new UncheckedIOException(e);
        }
    }
}
