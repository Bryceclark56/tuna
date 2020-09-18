package me.bc56.discord.model.gateway.payload.data;

import com.google.gson.JsonObject;
import com.google.gson.annotations.JsonAdapter;
import me.bc56.discord.adapter.DispatchPayloadDataAdapter;
import me.bc56.discord.util.Constants;

@JsonAdapter(DispatchPayloadDataAdapter.class)
public class DispatchPayloadData implements GatewayPayloadData {
    public transient static final int opCode = Constants.GatewayOpcodes.DISPATCH;

    JsonObject event;

    @Override
    public int getOpCode() {
        return opCode;
    }

    public JsonObject getEvent() {
        return event;
    }

    public void setEvent(JsonObject event) {
        this.event = event;
    }
}
