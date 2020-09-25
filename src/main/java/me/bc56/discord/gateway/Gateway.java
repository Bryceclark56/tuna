package me.bc56.discord.gateway;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.bc56.discord.Constants.GatewayOpcode;
import me.bc56.discord.events.type.DispatchEvent;
import me.bc56.discord.events.type.GatewayPayloadEvent;
import me.bc56.discord.gateway.dispatch.DispatchHandler;
import me.bc56.discord.gateway.dispatch.type.DispatchData;
import me.bc56.discord.gateway.payload.GatewayPayload;
import me.bc56.discord.gateway.payload.type.*;
import me.bc56.discord.thread.DiscordThreadManager;
import me.bc56.generic.event.EventDispatcher;
import me.bc56.generic.thread.Async;
import me.bc56.generic.event.Event;
import me.bc56.generic.event.EventSink;
import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

public class Gateway implements EventSink, Async {
    private static Logger log = LoggerFactory.getLogger(Gateway.class);

    final static Type gatewayPayloadType = new TypeToken<GatewayPayloadEvent<?>>(){}.getType();

    final BlockingQueue<Event> events = new LinkedBlockingQueue<>();

    final Gson gson;

    WebSocket gatewayWebsocket;
    final EventDispatcher eventDispatcher;
    final DispatchHandler dispatchHandler = new DispatchHandler();

    final ScheduledExecutorService heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();
    final AtomicBoolean hasReceivedAck = new AtomicBoolean(true); //Have we received a response to our last heartbeat?

    //TODO: Should this be a volatile Long instead?
    AtomicLong lastSequence;

    public Gateway(EventDispatcher dispatcher) {
        gson = new Gson()
                .newBuilder()
                .registerTypeAdapter(
                        Heartbeat.class, HeartbeatAdapter.class)
                .create();

        eventDispatcher = dispatcher;
    }

    @Override
    public void loop() {
        Event event;
        try {
            event = events.take();
        } catch (InterruptedException e) {
            log.error("Interrupted while waiting for new event", e);
            Thread.currentThread().interrupt();
            return;
        }

        // We assume this EventSink only received GatewayPayloadEvents
        // Might have to change this later

        if (event instanceof GatewayPayloadEvent<?> payloadEvent) {
            processPayload(payloadEvent.payload);
        }
    }

    public void connect() {
        WebSocketListener listener = new GatewayWebsocketListener(null);

        OkHttpClient client = new OkHttpClient();

        gatewayWebsocket = client.newWebSocket(null, listener);
    }

    //Wildcards and generics suck
    <E extends GatewayPayloadType> void processPayload(GatewayPayload<E> payload) {
        lastSequence.set(payload.sequence);

        switch (payload.opCode) {
            case DISPATCH -> {
                Dispatch<? extends DispatchData> data = (Dispatch<? extends DispatchData>) payload.data;
                handleDispatch(data);
            }
            case HEARTBEAT -> {
                Heartbeat data = (Heartbeat) payload.data;
                lastSequence.set(data.sequence);
                sendHeartbeat(data.sequence);
            }
            case RECONNECT -> {
                //TODO: https://discord.com/developers/docs/topics/gateway#reconnect
            }
            case INVALID_SESSION -> {
                //TODO: https://discord.com/developers/docs/topics/gateway#invalid-session
            }
            case HELLO -> {
                Hello data = (Hello) payload.data;
                setupHeartbeat(data.heartbeatInterval);
            }
            case HEARTBEAT_ACK -> hasReceivedAck.set(true);
        }
    }

    private <D extends DispatchData> void handleDispatch(Dispatch<D> payload) {
        DispatchEvent<D> event = new DispatchEvent<>(payload.data);

        eventDispatcher.dispatch(event);
        dispatchHandler.handleDispatch(payload.data);
    }

    private void setupHeartbeat(long interval) {
        heartbeatExecutor.scheduleAtFixedRate(
                () -> sendHeartbeat(lastSequence.get()),
                interval, //Initial delay
                interval, TimeUnit.MILLISECONDS //Period
        );
    }

    private void sendHeartbeat(Long sequence) {
        if (!hasReceivedAck.get()) {
            //TODO: Close (with non-1000 code) and re-open websocket
            //return
        }

        var heartbeat = new Heartbeat(sequence);
        GatewayPayload<Heartbeat> payload = new GatewayPayload<>(GatewayOpcode.HEARTBEAT, heartbeat, null, null);

        sendPayload(payload);
        hasReceivedAck.set(false);
    }

    //TODO: Is rate-limiting needed here?
    private <E extends GatewayPayloadType> void sendPayload(GatewayPayload<E> payload) {
        Type type = new TypeToken<GatewayPayload<E>>(){}.getType();
        String serialized = gson.toJson(payload, type);

        gatewayWebsocket.send(serialized);
    }

    @Override
    public void send(Event event) {
        events.add(event);
        DiscordThreadManager.quickRun(this);
    }
}
