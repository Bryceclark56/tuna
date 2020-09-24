package me.bc56.discord.api;

import me.bc56.discord.api.response.BotGatewayResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface DiscordService {
    @GET("/gateway/bot")
    Call<BotGatewayResponse> botGateway();
}
