package me.bc56.discord.model.api.response;

import com.google.gson.annotations.SerializedName;

public class BotGatewayResponse implements DiscordApiResponse {
    String url;

    int shards;

    @SerializedName("session_start_limit")
    SessionStartLimit sessionStartLimit;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getShards() {
        return shards;
    }

    public void setShards(int shards) {
        this.shards = shards;
    }

    public SessionStartLimit getSessionStartLimit() {
        return sessionStartLimit;
    }

    public void setSessionStartLimit(SessionStartLimit sessionStartLimit) {
        this.sessionStartLimit = sessionStartLimit;
    }

    public static class SessionStartLimit {
        int total;
        int remaining;

        @SerializedName("reset_after")
        int resetAfter;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getRemaining() {
            return remaining;
        }

        public void setRemaining(int remaining) {
            this.remaining = remaining;
        }

        public int getResetAfter() {
            return resetAfter;
        }

        public void setResetAfter(int resetAfter) {
            this.resetAfter = resetAfter;
        }
    }
}
