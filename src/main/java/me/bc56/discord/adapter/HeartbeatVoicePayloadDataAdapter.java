package me.bc56.discord.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.bc56.discord.model.voicegateway.payload.data.VoiceHeartbeatPayloadData;

import java.io.IOException;

public class HeartbeatVoicePayloadDataAdapter extends TypeAdapter<VoiceHeartbeatPayloadData> {

    @Override
    public void write(JsonWriter out, VoiceHeartbeatPayloadData value) throws IOException {
        out.value(value.getNonce());
    }


    @Override
    public VoiceHeartbeatPayloadData read(JsonReader in) throws IOException {
        final VoiceHeartbeatPayloadData payloadData = new VoiceHeartbeatPayloadData();

        payloadData.setNonce(in.nextInt());
        return payloadData;
    }
}
