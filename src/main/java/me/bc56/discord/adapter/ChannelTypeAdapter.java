package me.bc56.discord.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import me.bc56.discord.util.Constants.ChannelType;

public class ChannelTypeAdapter extends TypeAdapter<ChannelType> {

    @Override
    public void write(JsonWriter out, ChannelType channelType) throws IOException {
        out.value(channelType.typeNumber);
    }

    @Override
    public ChannelType read(JsonReader in) throws IOException {
        return ChannelType.valueOfTypeNumber(in.nextInt());
    }
}
