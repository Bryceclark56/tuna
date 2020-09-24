package me.bc56.discord.gateway.payload.type;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class HeartbeatAdapter extends TypeAdapter<Heartbeat> {

    @Override
    public void write(JsonWriter out, Heartbeat heartbeat) throws IOException {
        out.value(heartbeat.sequence);
    }

    @Override
    public Heartbeat read(JsonReader in) throws IOException {
        long sequence = in.nextLong();
        return new Heartbeat(sequence);
    }
}
