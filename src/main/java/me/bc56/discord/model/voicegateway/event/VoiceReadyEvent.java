package me.bc56.discord.model.voicegateway.event;

import me.bc56.discord.model.voicegateway.payload.VoiceGatewayPayload;
import me.bc56.discord.model.voicegateway.payload.data.ReadyVoicePayloadData;

public class VoiceReadyEvent implements VoiceGatewayEvent {

    private final int ssrc;

    private final String ip;

    private final int port;

    private final String[] modes;

    public VoiceReadyEvent(VoiceGatewayPayload payload) {
        ReadyVoicePayloadData readyPayload = (ReadyVoicePayloadData) payload.getEventData();

        ssrc = readyPayload.getSsrc();
        ip = readyPayload.getIp();
        port = readyPayload.getPort();
        modes = readyPayload.getModes();
    }

    public int getSsrc() {
        return ssrc;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String[] getModes() {
        return modes;
    }
}
