package me.bc56.discord.adapter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import me.bc56.discord.util.PayloadDataMap;
import me.bc56.discord.model.gateway.payload.GatewayPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;

public class GatewayPayloadAdapter extends TypeAdapter<GatewayPayload> {
    static Logger log = LoggerFactory.getLogger(GatewayPayloadAdapter.class);

    //Serialize
    @Override
    public void write(JsonWriter out, GatewayPayload payload) throws IOException {
        Gson gson = new Gson();
        Type dataType = PayloadDataMap.getMap().get(payload.getOpCode());

        out.beginObject();
        out.name("op").value(payload.getOpCode());
        out.name("d").jsonValue(gson.toJson(payload.getEventData(), dataType));
        out.name("t").jsonValue(payload.getEventName());
        out.name("s").value(payload.getSequence());
        out.endObject();
    }

    //Deserialize
    @Override
    public GatewayPayload read(JsonReader in) throws IOException {
        log.debug("Processing new payload...");

        final GatewayPayload payload = new GatewayPayload();
        Gson gson = new Gson();

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "op":
                    payload.setOpCode(in.nextInt());
                    log.debug("Found op: " + payload.getOpCode());
                    break;
                case "t":
                    if (in.peek() != JsonToken.NULL) {
                        String tString = in.nextString();
                        log.debug("T is: {}", tString);
                        payload.setEventName(tString);
                    } else {
                        log.debug("T is null");
                        in.nextNull();
                    }
                    break;
                case "d":
                    log.debug("Processing data...");
                    Type dataType = PayloadDataMap.getMap().get(payload.getOpCode());
                    if (dataType != null) {
                        log.debug("Type of: " + dataType.getTypeName());
                        payload.setEventData(gson.fromJson(in, dataType));
                        log.debug("Processed data!");
                    } else {
                        log.debug("Unable to discern type!!!!");
                    }
                    break;
                case "s":
                    log.debug("Sequence is " + in.peek());
                    if (in.peek() != JsonToken.NULL) {
                        payload.setSequence(in.nextInt());
                    } else {
                        in.nextNull();
                    }
                    break;
                default:
                    in.skipValue();
            }
        }
        in.endObject();

        log.debug("Finished processing payload!");

        return payload;
    }
}
