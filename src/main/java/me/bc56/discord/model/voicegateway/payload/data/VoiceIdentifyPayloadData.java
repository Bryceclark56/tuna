package me.bc56.discord.model.voicegateway.payload.data;

import com.google.gson.annotations.SerializedName;
import me.bc56.discord.util.Constants;

public class VoiceIdentifyPayloadData implements VoiceGatewayPayloadData {
    public transient static final int opCode = Constants.VoiceGatewayPayloadType.IDENTIFY;

    @SerializedName("server_id")
    String serverId;

    @SerializedName("user_id")
    String userId;

    @SerializedName("session_id")
    String sessionId;

    String token;

    @Override
    public int getOpCode() {
        return opCode;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
