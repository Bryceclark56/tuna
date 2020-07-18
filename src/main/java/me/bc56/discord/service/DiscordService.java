package me.bc56.discord.service;

import me.bc56.discord.model.ChannelMessage;
import me.bc56.discord.model.Guild;
import me.bc56.discord.model.api.request.ChannelMessageRequest;
import me.bc56.discord.model.api.response.BotGatewayResponse;
import retrofit2.Call;
import retrofit2.http.*;

public interface DiscordService {
    @GET("gateway/bot")
    Call<BotGatewayResponse> botGateway();

    @POST("channels/{channelId}/messages")
    Call<ChannelMessage> sendMessage(@Path("channelId") String channelId, @Body ChannelMessageRequest messageRequest);

    @GET("guilds/{guildId}")
    Call<Guild> getGuild(@Path("guildId") String guildId, @Query("with_counts") Boolean withCounts);
}
