package me.bc56.discord.model.voicegateway.payload.data;

import com.google.gson.annotations.SerializedName;
import me.bc56.discord.util.Constants;

public class VoiceSessionDescriptionPayloadData implements VoiceGatewayPayloadData {
    public transient static final int opCode = Constants.VoiceOpcodes.SESSION_DESCRIPTION;

    String mode;

    @SerializedName("secret_key")
    Byte[] secretKey;

    @Override
    public int getOpCode() {
        return opCode;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Byte[] getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(Byte[] secretKey) {
        this.secretKey = secretKey;
    }
}
