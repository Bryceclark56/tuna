package me.bc56.discord.model.voicegateway.payload.data;

import com.google.gson.annotations.JsonAdapter;
import me.bc56.discord.adapter.HeartbeatAckVoicePayloadDataAdapter;
import me.bc56.discord.util.Constants;

@JsonAdapter(HeartbeatAckVoicePayloadDataAdapter.class)
public class HeartbeatAckVoicePayloadData implements VoiceGatewayPayloadData {
    public transient static final int opCode = Constants.VoiceGatewayPayloadType.HEARTBEAT_ACK;

    long nonce;

    @Override
    public int getOpCode() {
        return opCode;
    }

    public long getNonce() {
        return nonce;
    }

    public void setNonce(long nonce) {
        this.nonce = nonce;
    }
}
