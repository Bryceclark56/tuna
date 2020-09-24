package me.bc56.discord.gateway.payload.type;

public class Heartbeat implements GatewayPayloadType {
    public final Long sequence;

    public Heartbeat(Long sequence) {
        this.sequence = sequence;
    }
}
