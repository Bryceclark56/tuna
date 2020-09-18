package me.bc56.discord;

import com.google.gson.Gson;
import me.bc56.discord.model.voicegateway.event.VoiceHeartbeatAckEvent;
import me.bc56.discord.model.voicegateway.event.VoiceHelloEvent;
import me.bc56.discord.model.voicegateway.event.VoiceReadyEvent;
import me.bc56.discord.model.voicegateway.event.VoiceSessionDescriptionEvent;
import me.bc56.discord.model.voicegateway.payload.VoiceGatewayPayload;
import me.bc56.discord.util.Constants.VoiceOpcodes;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

public class VoiceGatewayListener extends WebSocketListener {
    private static final Logger log = LoggerFactory.getLogger(VoiceGatewayListener.class);

    private final EventsManager eventsManager;

    public VoiceGatewayListener() {
        eventsManager = EventsManager.getInstance();
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        super.onOpen(webSocket, response);
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        super.onMessage(webSocket, text);
        log.debug("New message on voice websocket:\n{}", text);

        Gson gson = new Gson();

        VoiceGatewayPayload payload = gson.fromJson(text, VoiceGatewayPayload.class);
        int opCode = payload.getOpCode();

        //The server should only ever send us these events
        switch (opCode) {
            case VoiceOpcodes.READY:
                VoiceReadyEvent readyEvent = new VoiceReadyEvent(payload);
                eventsManager.emit(readyEvent);
                break;
            case VoiceOpcodes.SESSION_DESCRIPTION:
                VoiceSessionDescriptionEvent sessDescEvent = new VoiceSessionDescriptionEvent(payload);
                eventsManager.emit(sessDescEvent);
                break;
            case VoiceOpcodes.SPEAKING:
                //TODO: Handle this
                break;
            case VoiceOpcodes.HEARTBEAT_ACK:
                VoiceHeartbeatAckEvent ackEvent = new VoiceHeartbeatAckEvent(payload);
                eventsManager.emit(ackEvent);
                break;
            case VoiceOpcodes.HELLO:
                VoiceHelloEvent helloEvent = new VoiceHelloEvent(payload);
                eventsManager.emit(helloEvent);
                break;
            case VoiceOpcodes.RESUMED:
                //TODO: Handle this
                break;
            case VoiceOpcodes.CLIENT_DISCONNECT: {
                //Ignore this event for now
                System.exit(6);
            }
                break;
            default:
                log.warn("Unable to identify payload with op code {}", opCode);
                break;
        }
    }

    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        super.onClosed(webSocket, code, reason);
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
        super.onFailure(webSocket, t, response);
    }

    public void send(WebSocket webSocket, VoiceGatewayPayload payload) {
        Gson gson = new Gson();

        String payloadString = gson.toJson(payload);

        log.debug("Sending payload: " + payloadString);

        boolean sent = webSocket.send(payloadString);
        if (sent) {
            log.debug("Payload sent!");
        }
        else {
            log.error("Failed to send voice payload with OP {}", payload.getOpCode());
        }
    }
}
