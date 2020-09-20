package me.bc56.discord.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.bc56.discord.model.Snowflake;

import java.io.IOException;

public class SnowflakeAdapter extends TypeAdapter<Snowflake> {

    @Override
    public void write(JsonWriter out, Snowflake snowflake) throws IOException {
        out.value(snowflake.toString());
    }

    @Override
    public Snowflake read(JsonReader in) throws IOException {
        return new Snowflake(in.nextString());
    }
}
