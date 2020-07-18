package me.bc56.discord.model.gateway.payload;

import com.google.gson.annotations.SerializedName;
import me.bc56.discord.model.DiscordUser;

public class ReadyPayload {
    @SerializedName("v")
    int gatewayVersion;

    DiscordUser user;

    //private_channels

    UnavailableGuild[] guilds;

    @SerializedName("session_id")
    String sessionId;

    int[] shard;

    public int getGatewayVersion() {
        return gatewayVersion;
    }

    public void setGatewayVersion(int gatewayVersion) {
        this.gatewayVersion = gatewayVersion;
    }

    public DiscordUser getUser() {
        return user;
    }

    public void setUser(DiscordUser user) {
        this.user = user;
    }

    public UnavailableGuild[] getGuilds() {
        return guilds;
    }

    public void setGuilds(UnavailableGuild[] guilds) {
        this.guilds = guilds;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public static class UnavailableGuild {
        String id;
        Boolean unavailable;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Boolean getUnavailable() {
            return unavailable;
        }

        public void setUnavailable(Boolean unavailable) {
            this.unavailable = unavailable;
        }
    }
}
