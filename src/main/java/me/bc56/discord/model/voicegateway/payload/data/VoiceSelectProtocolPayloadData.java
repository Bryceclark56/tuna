package me.bc56.discord.model.voicegateway.payload.data;

import me.bc56.discord.util.Constants;

public class VoiceSelectProtocolPayloadData implements VoiceGatewayPayloadData {
    public transient static final int opCode = Constants.VoiceGatewayPayloadType.SELECT_PROTOCOL;

    String protocol;

    Data data;

    @Override
    public int getOpCode() {
        return opCode;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        String address;
        Integer port;
        String mode;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }
    }
}
