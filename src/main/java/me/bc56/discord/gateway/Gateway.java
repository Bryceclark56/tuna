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

    //Some may not be dispatches, but full gateway payload events (Discord docs on it aren't clear on this)
    private static Map<GatewayDispatch, Class<? extends DispatchData>> dispatchMap = Map.ofEntries(
            Map.entry(READY, Ready.class),
            Map.entry(RESUMED, Resumed.class),
            Map.entry(CHANNEL_CREATE, ChannelCreate.class),
            Map.entry(CHANNEL_UPDATE, ChannelUpdate.class),
            Map.entry(CHANNEL_DELETE, ChannelDelete.class),
            Map.entry(CHANNEL_PINS_UPDATE, ChannelPinsUpdate.class),
            Map.entry(GUILD_CREATE, GuildCreate.class),
            Map.entry(GUILD_UPDATE, GuildUpdate.class),
            Map.entry(GUILD_DELETE, GuildDelete.class),
            Map.entry(GUILD_BAN_ADD, GuildBanAdd.class),
            Map.entry(GUILD_BAN_REMOVE, GuildBanRemove.class),
            Map.entry(GUILD_EMOJIS_UPDATE, GuildEmojisUpdate.class),
            Map.entry(GUILD_INTEGRATIONS_UPDATE, GuildIntegrationsUpdate.class),
            Map.entry(GUILD_MEMBER_ADD, GuildMemberAdd.class),
            Map.entry(GUILD_MEMBER_REMOVE, GuildMemberRemove.class),
            Map.entry(GUILD_MEMBER_UPDATE, GuildMemberUpdate.class),
            Map.entry(GUILD_MEMBERS_CHUNK, GuildMembersChunk.class),
            Map.entry(GUILD_ROLE_CREATE, GuildRoleCreate.class),
            Map.entry(GUILD_ROLE_UPDATE, GuildRoleUpdate.class),
            Map.entry(GUILD_ROLE_DELETE, GuildRoleDelete.class),
            Map.entry(INVITE_CREATE, InviteCreate.class),
            Map.entry(INVITE_DELETE, InviteDelete.class),
            Map.entry(MESSAGE_CREATE, MessageCreate.class),
            Map.entry(MESSAGE_UPDATE, MessageUpdate.class),
            Map.entry(MESSAGE_DELETE, MessageDelete.class),
            Map.entry(MESSAGE_DELETE_BULK, MessageDeleteBulk.class),
            Map.entry(MESSAGE_REACTION_ADD, MessageReactionAdd.class),
            Map.entry(MESSAGE_REACTION_REMOVE, MessageReactionRemove.class),
            Map.entry(MESSAGE_REACTION_REMOVE_ALL, MessageReactionRemoveAll.class),
            Map.entry(MESSAGE_REACTION_REMOVE_EMOJI, MessageReactionRemoveEmoji.class),
            Map.entry(TYPING_START, TypingStart.class),
            Map.entry(USER_UPDATE, UserUpdate.class),
            Map.entry(VOICE_SERVER_UPDATE, VoiceServerUpdate.class),
            Map.entry(WEBHOOKS_UPDATE, WebhooksUpdate.class)
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
