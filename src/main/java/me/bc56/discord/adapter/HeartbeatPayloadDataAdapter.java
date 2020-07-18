package me.bc56.discord.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.bc56.discord.model.gateway.payload.data.HeartbeatPayloadData;

import java.io.IOException;

public class HeartbeatPayloadDataAdapter extends TypeAdapter<HeartbeatPayloadData> {

    @Override
    public void write(JsonWriter out, HeartbeatPayloadData value) throws IOException {
        out.value(value.getNonce());
    }


    @Override
    public HeartbeatPayloadData read(JsonReader in) throws IOException {
        return null;
    }
}
