package me.bc56.discord.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.bc56.discord.model.voicegateway.payload.data.HeartbeatVoicePayloadData;

import java.io.IOException;

public class HeartbeatVoicePayloadDataAdapter extends TypeAdapter<HeartbeatVoicePayloadData> {

    @Override
    public void write(JsonWriter out, HeartbeatVoicePayloadData value) throws IOException {
        out.value(value.getNonce());
    }


    @Override
    public HeartbeatVoicePayloadData read(JsonReader in) throws IOException {
        final HeartbeatVoicePayloadData payloadData = new HeartbeatVoicePayloadData();

        payloadData.setNonce(in.nextInt());
        return payloadData;
    }
}
