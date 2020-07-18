package me.bc56.discord.factory;

import me.bc56.discord.DiscordBot;
import me.bc56.discord.DiscordServiceInterceptor;
import retrofit2.Retrofit;

public class DiscordBotFactory {
    private String authToken = "";
    private String userAgent = "";
    private Retrofit retrofitClient = null;

    public DiscordBot build() {
        if (userAgent.isEmpty()) {
            userAgent = "GenericBot (example.com, 0.1.0)";
        }

        if (retrofitClient == null) {
            DiscordServiceInterceptor interceptor =
                    new DiscordServiceInterceptor(authToken, userAgent);

            retrofitClient =
                    new RetrofitFactory()
                    .newBotClient(interceptor);
        }

        return new DiscordBot(authToken, userAgent, retrofitClient);
    }

    public DiscordBotFactory authToken(String token) {
        this.authToken = token;
        return this;
    }

    public DiscordBotFactory userAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public DiscordBotFactory retrofit(Retrofit client) {
        this.retrofitClient = client;
        return this;
    }
}
