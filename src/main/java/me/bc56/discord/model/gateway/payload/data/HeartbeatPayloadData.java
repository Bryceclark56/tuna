package me.bc56.discord.model.gateway.payload.data;

import com.google.gson.annotations.JsonAdapter;
import me.bc56.discord.adapter.HeartbeatPayloadDataAdapter;
import me.bc56.discord.util.Constants;

@JsonAdapter(HeartbeatPayloadDataAdapter.class)
public class HeartbeatPayloadData implements GatewayPayloadData {
    public transient static final int opCode = Constants.GatewayPayloadType.HEARTBEAT;

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
