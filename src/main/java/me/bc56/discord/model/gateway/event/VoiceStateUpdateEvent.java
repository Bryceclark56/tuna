package me.bc56.discord.model.gateway.event;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.bc56.discord.model.VoiceState;
import me.bc56.discord.model.gateway.payload.GatewayPayload;
import me.bc56.discord.model.gateway.payload.data.VoiceStateUpdatePayloadData;

public class VoiceStateUpdateEvent implements GatewayEvent {

    VoiceState voiceState;

    public VoiceStateUpdateEvent(DispatchEvent dispatchEvent) {
        Gson gson = new Gson();
        voiceState = gson.fromJson(dispatchEvent.getEventData(), VoiceState.class);
    }

    public VoiceState getVoiceState() {
        return voiceState;
    }
}
