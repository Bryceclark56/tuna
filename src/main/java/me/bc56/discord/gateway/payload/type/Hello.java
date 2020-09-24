package me.bc56.discord.gateway.payload.type;

public class Hello implements GatewayPayloadType {
    public final long heartbeatInterval;

    public Hello(long heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }
}
