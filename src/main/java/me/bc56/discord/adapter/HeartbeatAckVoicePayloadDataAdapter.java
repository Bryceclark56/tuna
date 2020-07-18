package me.bc56.discord.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.bc56.discord.model.voicegateway.payload.data.VoiceHeartbeatAckPayloadData;

import java.io.IOException;

public class HeartbeatAckVoicePayloadDataAdapter extends TypeAdapter<VoiceHeartbeatAckPayloadData> {

    @Override
    public void write(JsonWriter out, VoiceHeartbeatAckPayloadData value) throws IOException {
        out.value(value.getNonce());
    }

    @Override
    public VoiceHeartbeatAckPayloadData read(JsonReader in) throws IOException {
        final VoiceHeartbeatAckPayloadData payloadData = new VoiceHeartbeatAckPayloadData();

        payloadData.setNonce(in.nextInt());
        return payloadData;
    }
}
