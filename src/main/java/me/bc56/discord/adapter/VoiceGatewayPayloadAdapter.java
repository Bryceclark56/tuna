package me.bc56.discord.adapter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.bc56.discord.model.voicegateway.payload.VoiceGatewayPayload;
import me.bc56.discord.model.voicegateway.payload.data.VoiceHeartbeatAckPayloadData;
import me.bc56.discord.util.PayloadDataMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;

public class VoiceGatewayPayloadAdapter extends TypeAdapter<VoiceGatewayPayload> {
    static Logger log = LoggerFactory.getLogger(GatewayPayloadAdapter.class);

    //Serialize
    @Override
    public void write(JsonWriter out, VoiceGatewayPayload payload) throws IOException {
        Gson gson = new Gson();
        Type dataType = PayloadDataMap.getMap().get(payload.getOpCode());

        out.beginObject();
        out.name("op").value(payload.getOpCode());
        out.name("d").jsonValue(gson.toJson(payload.getEventData(), dataType));
        out.endObject();
    }

    //Deserialize
    @Override
    public VoiceGatewayPayload read(JsonReader in) throws IOException {
        log.debug("Processing new voice payload...");

        final VoiceGatewayPayload payload = new VoiceGatewayPayload();
        Gson gson = new Gson();

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "op":
                    payload.setOpCode(in.nextInt());
                    log.debug("Found op: " + payload.getOpCode());
                    break;
                case "d":
                    log.debug("Processing data...");
                    Type dataType = PayloadDataMap.getVoiceMap().get(payload.getOpCode());
                    if (dataType != null) {
                        log.debug("Type of: " + dataType.getTypeName());

                        //Special case for heartbeat ACK
                        String simpleName = dataType.getClass().getSimpleName();
                        if (simpleName.equals(VoiceHeartbeatAckPayloadData.class.getSimpleName())) {
                            VoiceHeartbeatAckPayloadData voiceHeartbeatAckPayloadData = new VoiceHeartbeatAckPayloadData();
                        }

                        payload.setEventData(gson.fromJson(in, dataType));
                        log.debug("Processed data!");
                    }
                    else {
                        log.debug("Unable to discern type!!!!");
                    }
                    break;
                default:
                    in.skipValue();
            }
        }
        in.endObject();

        log.debug("Finished processing voice payload!");

        return payload;
    }
}
