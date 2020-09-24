package me.bc56.discord.gateway;

import checkers.nullness.quals.NonNull;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import me.bc56.discord.Constants.GatewayOpcode;
import me.bc56.discord.events.type.GatewayPayloadEvent;
import me.bc56.discord.gateway.payload.GatewayPayload;
import me.bc56.discord.gateway.payload.type.*;
import me.bc56.generic.event.EventDispatcher;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import javax.annotation.Nullable;
import java.lang.reflect.Type;

public class GatewayWebsocketListener extends WebSocketListener {

    EventDispatcher eventDispatcher;

    public GatewayWebsocketListener(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
        super.onOpen(webSocket, response);
    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
        super.onMessage(webSocket, text);

        var gson = new Gson();

        var object = gson.fromJson(text, JsonObject.class);
        int op = object.get("op").getAsInt();
        Type payloadType = getPayloadType(GatewayOpcode.getAsEnum(op));

        var event = new GatewayPayloadEvent<>(gson.fromJson(object, payloadType));

        eventDispatcher.dispatch(event);
    }

    @Override
    public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        super.onClosing(webSocket, code, reason);
    }

    @Override
    public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        super.onClosed(webSocket, code, reason);
    }

    @Override
    public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
        super.onFailure(webSocket, t, response);
    }

    public static Type getPayloadType(GatewayOpcode op) {
        TypeToken<?> typeToken = switch(op) {
            case DISPATCH -> new TypeToken<GatewayPayload<Dispatch>>(){};
            case HEARTBEAT -> new TypeToken<GatewayPayload<Heartbeat>>() {};
            case IDENTIFY -> new TypeToken<GatewayPayload<Identify>>(){};
            case PRESENCE_UPDATE -> new TypeToken<GatewayPayload<PresenceUpdate>>(){};
            case VOICE_STATE_UPDATE -> new TypeToken<GatewayPayload<VoiceStateUpdate>>(){};
            case RESUME -> new TypeToken<GatewayPayload<Resume>>(){};
            case RECONNECT -> new TypeToken<GatewayPayload<Reconnect>>(){};
            case REQUEST_GUILD_MEMBERS -> new TypeToken<GatewayPayload<RequestGuildMembers>>(){};
            case INVALID_SESSION -> new TypeToken<GatewayPayload<InvalidSession>>(){};
            case HELLO -> new TypeToken<GatewayPayload<Hello>>(){};
            case HEARTBEAT_ACK -> new TypeToken<GatewayPayload<HeartbeatAck>>(){};
        };

        return typeToken.getType();
    }
}
