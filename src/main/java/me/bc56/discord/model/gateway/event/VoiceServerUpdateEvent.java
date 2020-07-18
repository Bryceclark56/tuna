package me.bc56.discord.model.gateway.event;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class VoiceServerUpdateEvent extends DispatchEvent {
    String token;

    @SerializedName("guild_id")
    String guildId;

    String endpoint;

    public VoiceServerUpdateEvent(DispatchEvent dispatchEvent) {
        super(dispatchEvent.getEventName(), dispatchEvent.getEventData());

        JsonObject eventObj = dispatchEvent.getEventData();

        token = eventObj.get("token").getAsString();
        guildId = eventObj.get("guild_id").getAsString();
        endpoint = eventObj.get("endpoint").getAsString();
    }

    public String getToken() {
        return token;
    }

    public String getGuildId() {
        return guildId;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
