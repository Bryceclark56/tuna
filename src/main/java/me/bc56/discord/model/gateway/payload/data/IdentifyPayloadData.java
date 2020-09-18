package me.bc56.discord.model.gateway.payload.data;

import me.bc56.discord.util.Constants;

public class IdentifyPayloadData implements GatewayPayloadData {
    public transient static final int opCode = Constants.GatewayOpcodes.IDENTIFY;

    private String token;

    private ConnectionProperties properties;

    private Boolean compress;

    private String presence;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ConnectionProperties getProperties() {
        return properties;
    }

    public void setProperties(ConnectionProperties properties) {
        this.properties = properties;
    }

    public Boolean getCompress() {
        return compress;
    }

    public void setCompress(Boolean compress) {
        this.compress = compress;
    }

    public String getPresence() {
        return presence;
    }

    public void setPresence(String presence) {
        this.presence = presence;
    }

    @Override
    public int getOpCode() {
        return opCode;
    }
}
