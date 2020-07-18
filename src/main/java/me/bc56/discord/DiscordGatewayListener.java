package me.bc56.discord;

import com.google.gson.Gson;
import me.bc56.discord.model.gateway.event.DispatchEvent;
import me.bc56.discord.model.gateway.event.HeartbeakAckEvent;
import me.bc56.discord.model.gateway.event.HelloEvent;
import me.bc56.discord.model.gateway.payload.GatewayPayload;
import me.bc56.discord.model.gateway.payload.data.*;
import me.bc56.discord.util.Constants;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DiscordGatewayListener extends WebSocketListener {
    static Logger log = LoggerFactory.getLogger(DiscordGatewayListener.class);

    public static final int[] validOps = new int[] {0, 1, 7, 9, 10, 11}; //TODO: Is this still needed???

    private EventsManager eventEmitter;

    private long heartbeatInterval;
    private Timer heartbeatTimer;
    private ScheduledExecutorService heartbeatScheduler;

    public boolean expectingReadyEvent;

    private final String authToken;

    public DiscordGatewayListener(String authToken) {
        this.authToken = authToken;

        eventEmitter = EventsManager.getInstance();
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        super.onOpen(webSocket, response);

        log.debug("Opening socket...");

        expectingReadyEvent = false;
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        super.onMessage(webSocket, text);

        log.debug("New payload: " + text);

        Gson gson = new Gson();

        log.debug("Deserializing payload...");
        GatewayPayload payload = gson.fromJson(text, GatewayPayload.class);

        log.debug("Payload opCode: " + payload.getOpCode());

        if (Arrays.binarySearch(validOps, payload.getOpCode()) >= 0) {
            //Handle other payloads
            GatewayPayloadData data = payload.getEventData();

            switch (payload.getOpCode()) {
                case Constants.GatewayPayloadType.HELLO:
                    log.debug("Type of Hello!");
                    heartbeatInterval = ((HelloPayloadData) data).getHeartbeatInterval();
                    startHeartbeat(webSocket, heartbeatInterval);
                    eventEmitter.emit(new HelloEvent());
                    break;
                case Constants.GatewayPayloadType.HEARTBEAT_ACK:
                    log.debug("Recieved ACK!");
                    eventEmitter.emit(new HeartbeakAckEvent());
                    break;
                case Constants.GatewayPayloadType.DISPATCH:
                    DispatchPayloadData dispatchData = (DispatchPayloadData) data;
                    //log.debug("Recieved dispatch event with name: {}\nRAW Payload: {}\nEND_OF_PAYLOAD", payload.getEventName(), text);
                    eventEmitter.emit(new DispatchEvent(
                            payload.getEventName(), dispatchData.getEvent()
                    ));
            }
        }
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        super.onClosed(webSocket, code, reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        super.onFailure(webSocket, t, response);
    }

    public void send(WebSocket webSocket, GatewayPayload payload) {
        Gson gson = new Gson();

        String payloadString = gson.toJson(payload);

        log.debug("Sending payload: " + payloadString);

        boolean sent = webSocket.send(payloadString);
        if (sent) {
            log.debug("Payload sent!");
        }
        else {
            log.error("Failed to send payload with OP {}", payload.getOpCode());
        }
    }

    private void startHeartbeat(WebSocket webSocket, long interval) {
        log.debug("Starting heartbeat...");

        if (heartbeatScheduler == null) {
            heartbeatScheduler = Executors.newSingleThreadScheduledExecutor();
        }

        Runnable task = () -> sendHeartbeat(webSocket);

        heartbeatScheduler.scheduleAtFixedRate(task, interval, interval, TimeUnit.MILLISECONDS);
    }

    private void sendHeartbeat(WebSocket webSocket) {
        log.debug("Sending heartbeat...");

        GatewayPayload payload = new GatewayPayload();

        HeartbeatPayloadData heartbeatPayloadData = new HeartbeatPayloadData();
        payload.setEventData(heartbeatPayloadData);

        payload.setOpCode(heartbeatPayloadData.getOpCode());

        send(webSocket, payload);

        //TODO: Ensure we receive Heartbeat ACK in response
    }

    public EventsManager getEventEmitter() {
        return eventEmitter;
    }

    public void setEventEmitter(EventsManager eventEmitter) {
        this.eventEmitter = eventEmitter;
    }
}
