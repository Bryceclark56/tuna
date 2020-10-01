package me.bc56.discord.gateway;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.EnumSet;

public class GatewayIntentAdapter extends TypeAdapter<EnumSet<GatewayIntent>> {
    @Override
    public void write(JsonWriter out, EnumSet<GatewayIntent> value) throws IOException {
        out.value(GatewayIntent.serializeSet(value));
    }

    @Override
    public EnumSet<GatewayIntent> read(JsonReader in) throws IOException {
        return GatewayIntent.deserializeSet(in.nextInt());
    }
}
