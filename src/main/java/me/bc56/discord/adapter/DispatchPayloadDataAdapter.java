package me.bc56.discord.adapter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.bc56.discord.model.gateway.payload.data.DispatchPayloadData;

import java.io.IOException;

public class DispatchPayloadDataAdapter extends TypeAdapter<DispatchPayloadData> {

    @Override
    public void write(JsonWriter out, DispatchPayloadData value) throws IOException {

    }

    @Override
    public DispatchPayloadData read(JsonReader in) throws IOException {
        Gson gson = new Gson();
        DispatchPayloadData data = new DispatchPayloadData();

        JsonObject eventObj = gson.fromJson(in, JsonObject.class);



        data.setEvent(eventObj);

        return data;
    }
}
