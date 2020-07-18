package me.bc56.discord.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.bc56.discord.model.voicegateway.payload.data.HeartbeatAckVoicePayloadData;

import java.io.IOException;

public class HeartbeatAckVoicePayloadDataAdapter extends TypeAdapter<HeartbeatAckVoicePayloadData> {

    @Override
    public void write(JsonWriter out, HeartbeatAckVoicePayloadData value) throws IOException {
        out.value(value.getNonce());
    }

    @Override
    public HeartbeatAckVoicePayloadData read(JsonReader in) throws IOException {
        final HeartbeatAckVoicePayloadData payloadData = new HeartbeatAckVoicePayloadData();

        payloadData.setNonce(in.nextInt());
        return payloadData;
    }
}
