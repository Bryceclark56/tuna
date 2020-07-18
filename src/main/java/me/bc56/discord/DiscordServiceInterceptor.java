package me.bc56.discord;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class DiscordServiceInterceptor implements Interceptor {

    // Discord authorization token
    private final String authToken;

    // The User-Agent header must be formatted according to
    // https://discordapp.com/developers/docs/reference#user-agent
    //TODO: Force formatting
    private final String userAgent;

    public DiscordServiceInterceptor(String authToken, String userAgent) {
        this.authToken = authToken;
        this.userAgent = userAgent;
    }

    // For every request to the Discord API, an Authorization and User-Agent header is added.
    @Override
    public @NotNull Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        //Handles normal HTTP API requests
        request = request.newBuilder()
                .addHeader("Authorization", "Bot " + authToken)
                .addHeader("User-Agent", userAgent)
                .build();

        return chain.proceed(request);
    }
}
