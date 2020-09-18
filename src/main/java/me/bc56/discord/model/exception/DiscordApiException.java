package me.bc56.discord.model.exception;

import me.bc56.discord.api.DiscordApiAccessor;

//Bad HTTP Response from Discord HTTP API
public class DiscordApiException extends Exception {
    //HTTP Response
    public final int httpError;

    //JSON Response
    public final int code;
    public final String message;

    public DiscordApiException(int httpErrorCode, int jsonErrorCode, String jsonErrorMessage) {
        httpError = httpErrorCode;
        code = jsonErrorCode;
        message = jsonErrorMessage;
    }
}
