package me.bc56.discord.gateway;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.bc56.discord.Constants.GatewayDispatch;
import me.bc56.discord.Constants.GatewayOpcode;
import me.bc56.discord.events.type.GatewayPayloadEvent;
import me.bc56.discord.gateway.dispatch.*;
import me.bc56.discord.gateway.payload.GatewayPayload;
import me.bc56.discord.gateway.payload.type.*;
import me.bc56.discord.thread.DiscordThreadManager;
import me.bc56.generic.thread.Async;
import me.bc56.generic.event.Event;
import me.bc56.generic.event.EventSink;
import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static me.bc56.discord.Constants.GatewayDispatch.*;

public class Gateway implements EventSink, Async {
    private static Logger log = LoggerFactory.getLogger(Gateway.class);

    private static Map<GatewayDispatch, Class<? extends DispatchData>> dispatchMap = Map.of(
            READY, Ready.class,
            RESUMED, Resumed.class,
            RECONNECT,
            CHANNEL_CREATE, ChannelCreate.class,
            CHANNEL_UPDATE, ChannelUpdate.class,
            CHANNEL_DELETE, ChannelDelete.class,
            CHANNEL_PINS_UPDATE,
            GUILD_CREATE,
            GUILD_UPDATE,
            GUILD_DELETE,
            GUILD_BAN_ADD,
            GUILD_EMOJIS_UPDATE,
            GUILD_INTEGRATIONS_UPDATE,
            GUILD_MEMBER_ADD,
            GUILD_MEMBER_REMOVE,
            GUILD_MEMBER_UPDATE,
            GUILD_MEMBERS_CHUNK,
            GUILD_ROLE_CREATE,
            GUILD_ROLE_UPDATE,
            GUILD_ROLE_DELETE,
            INVITE_CREATE,
            INVITE_DELETE,
            MESSAGE_CREATE,
            MESSAGE_UPDATE,
            MESSAGE_DELETE,
            MESSAGE_DELETE_BULK,
            MESSAGE_REACTION_ADD,
            MESSAGE_REACTION_REMOVE,
            MESSAGE_REACTION_REMOVE_ALL,
            MESSAGE_REACTION_REMOVE_EMOJI,
            PRESENCE_UPDATE,
            TYPING_START,
            USER_UPDATE,
            VOICE_STATE_UPDATE,
            VOICE_SERVER_UPDATE,
            WEBHOOKS_UPDATE
    );

    BlockingQueue<Event> events = new LinkedBlockingQueue<>();

    Gson gson;

    WebSocket gatewayWebsocket;

    ScheduledExecutorService heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();
    AtomicBoolean hasReceivedAck = new AtomicBoolean(true); //Have we received a response to our last heartbeat?

    //TODO: Should this be a volatile Long instead?
    AtomicLong lastSequence;

    public Gateway() {
        gson = new Gson()
                .newBuilder()
                .registerTypeAdapter(
                        Heartbeat.class, HeartbeatAdapter.class)
                .create();
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
        processEvent((GatewayPayloadEvent<? extends GatewayPayloadType>) event);
    }

    public void connect() {
        WebSocketListener listener = new GatewayWebsocketListener(null);

        OkHttpClient client = new OkHttpClient();

        gatewayWebsocket = client.newWebSocket(null, listener);
    }

    void processEvent(GatewayPayloadEvent<? extends GatewayPayloadType> event) {
        processPayload(event.payload);
    }

    //Wildcards and generics suck
    <E extends GatewayPayloadType> void processPayload(GatewayPayload<E> payload) {
        lastSequence.set(payload.sequence);

        switch (payload.opCode) {
            case DISPATCH -> {
                //TODO:
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
