package me.bc56.discord.model.voicegateway.payload.data;

import me.bc56.discord.util.Constants;

public class VoiceSpeakingPayloadData implements VoiceGatewayPayloadData {
    public transient static final int opCode = Constants.VoiceOpcodes.SPEAKING;

    Integer speaking;

    Integer delay;

    Integer ssrc;

    @Override
    public int getOpCode() {
        return opCode;
    }

    public int getSpeaking() {
        return speaking;
    }

    public void setSpeaking(int speaking) {
        this.speaking = speaking;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getSsrc() {
        return ssrc;
    }

    public void setSsrc(int ssrc) {
        this.ssrc = ssrc;
    }
}
