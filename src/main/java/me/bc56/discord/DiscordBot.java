package me.bc56.discord;

import me.bc56.discord.model.ChannelMessage;
import me.bc56.discord.model.DiscordUser;
import me.bc56.discord.model.Guild;
import me.bc56.discord.model.api.request.ChannelMessageRequest;
import me.bc56.discord.model.gateway.event.*;
import me.bc56.discord.model.gateway.payload.GatewayPayload;
import me.bc56.discord.model.gateway.payload.data.ConnectionProperties;
import me.bc56.discord.model.gateway.payload.data.IdentifyPayloadData;
import me.bc56.discord.model.api.response.BotGatewayResponse;

import me.bc56.discord.model.gateway.payload.data.VoiceStateUpdatePayloadData;
import me.bc56.discord.model.voicegateway.event.VoiceHelloEvent;
import me.bc56.discord.model.voicegateway.payload.VoiceGatewayPayload;
import me.bc56.discord.model.voicegateway.payload.data.VoiceHeartbeatPayloadData;
import me.bc56.discord.service.DiscordService;
import me.bc56.discord.util.Constants;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class DiscordBot {
    static Logger log = LoggerFactory.getLogger(DiscordBot.class);

    private String authToken;
    private String userAgent;

    private DiscordUser currentUser;

    private Retrofit retrofit;
    private DiscordService discordService;

    private String state;

    private WebSocket webSocket;
    private WebSocket voiceWebSocket;

    private DiscordGatewayListener gateway;
    private VoiceGatewayListener voiceGateway;

    private EventsManager eventEmitters;

    private ScheduledExecutorService voiceHeartbeatScheduler;

    private long heartbeatNonce;

    public DiscordBot(String authToken, String userAgent, Retrofit retrofit) {
        this.state = "STOPPED";

        this.authToken = authToken;
        this.userAgent = userAgent;
        this.retrofit = retrofit;

        this.discordService = this.retrofit.create(DiscordService.class);

        this.heartbeatNonce = 0;

        eventEmitters = EventsManager.getInstance();

        log.debug("Initializing gateway listener...");
        gateway = new DiscordGatewayListener(authToken);

        registerInternalEvents();
    }

    public void start() {
        log.debug("Opening websocket...");
        webSocket = new OkHttpClient().newWebSocket(getGatewayRequest(), gateway);

        this.state = "RUNNING";
    }

    public void stop() {
        log.debug("Setting bot state to STOPPED");
        state = "STOPPED";
        webSocket.close(1000, "Stopping");
    }

    public boolean isStopped() {
        return state.equals("STOPPED");
    }

    private void registerInternalEvents() {
        log.debug("Registering internal event emitters...");

        eventEmitters.register(HelloEvent.class, event -> sendIdentify());
        eventEmitters.register(DispatchEvent.class, this::parseDispatch);
        eventEmitters.register(ReadyEvent.class, event -> {
            currentUser = event.getUser();

            log.info("Connected as {}", currentUser.getUsername());
        });

        //Register internal voice events
        eventEmitters.register(VoiceHelloEvent.class, event -> {
            setupVoiceHeartbeat(event.getHeartbeatInterval());
        });
    }

    public void connectToVoiceChannel(String guildId, String channelId) {
        voiceGateway = new VoiceGatewayListener();

        VoiceStateUpdatePayloadData voiceStateUpdatePayloadData = new VoiceStateUpdatePayloadData();
        voiceStateUpdatePayloadData.setGuildId(guildId);
        voiceStateUpdatePayloadData.setChannelId(channelId);
        voiceStateUpdatePayloadData.setSelfDeaf(false);
        voiceStateUpdatePayloadData.setSelfMute(false);

        GatewayPayload payload = new GatewayPayload();
        payload.setOpCode(Constants.GatewayPayloadType.VOICE_STATE_UPDATE);
        payload.setEventData(voiceStateUpdatePayloadData);

        eventEmitters.register(VoiceServerUpdateEvent.class, (event) -> {
            if (voiceWebSocket == null) {
                voiceWebSocket = new OkHttpClient().newWebSocket(getVoiceGatewayRequest(event.getEndpoint()), gateway);
            }
        });
        gateway.send(webSocket, payload);
    }

    public void parseDispatch(DispatchEvent event) {
        log.debug("New dispatch event!");

        if (event == null) {
            log.warn("Dispatch event was null :/");
            return;
        }

        log.debug("Event: {}", event.getEventName());
        log.debug("Event data: {}", event.getEventData());

        if (event.getEventName() == null) {
            return;
        }

        String eventName = event.getEventName();

        switch (eventName) {
            case "MESSAGE_CREATE":
                MessageCreateEvent msgEvent = new MessageCreateEvent(event);
                ChannelMessage msg = msgEvent.getMessage();

                log.debug("New message from channel {} by user {} that says: \n{}\nEND_OF_MESSAGE",
                        msg.getChannelId(), msg.getAuthor().getUsername(), msg.getContent());

                eventEmitters.emit(msgEvent);
                break;
            case "READY":
                ReadyEvent readyEvent = new ReadyEvent(event);

                eventEmitters.emit(readyEvent);
                break;
            case "VOICE_SERVER_UPDATE":
                VoiceServerUpdateEvent voiceUpdate = new VoiceServerUpdateEvent(event);

                eventEmitters.emit(voiceUpdate);
                break;
        }

        log.debug("Finished processing dispatch");
    }

    //Called by external clients to register events
    public <E extends GatewayEvent> void on(Class<E> event, Consumer<E> callback) {
        eventEmitters.register(event, callback);
    }

    public void sendMessage(String channel, String content) {
        ChannelMessageRequest request = new ChannelMessageRequest();
        request.setContent(content);

        log.debug("Sending message to Discord channel {} with content \"{}\"", channel, content);
        Call<ChannelMessage> messageCall = discordService.sendMessage(channel, request);

        messageCall.enqueue(new Callback<ChannelMessage>() {
            @Override
            public void onResponse(@NotNull Call<ChannelMessage> call, @NotNull Response<ChannelMessage> response) {
                log.debug("Successfully sent message!");
            }

            @Override
            public void onFailure(@NotNull Call<ChannelMessage> call, Throwable t) {
                log.debug("Failed to send message!");
            }
        });
    }

    public Guild getGuild(String guildId) {
        log.debug("Getting guild {}", guildId);

        Call<Guild> guildRequest = discordService.getGuild(guildId, null);

        try {
            Response<Guild> guildResponse = guildRequest.execute();

            if (guildResponse.isSuccessful()) {
                return guildResponse.body();
            }
        }
        catch (IOException e) {
            log.warn("Unable to get guild {}", guildId);
        }

        return null;
    }

    public void sendIdentify() {
        log.debug("Sending identify...");

        IdentifyPayloadData data = new IdentifyPayloadData();
        data.setToken(authToken);

        ConnectionProperties properties = new ConnectionProperties();
        properties.setOs("Windows");
        properties.setBrowser("GenericBot");
        properties.setDevice("GenericBot");

        data.setProperties(properties);
        data.setCompress(false);

        GatewayPayload payload = new GatewayPayload();
        payload.setOpCode(data.getOpCode());
        payload.setEventData(data);

        gateway.send(webSocket, payload);
        gateway.expectingReadyEvent = true;
    }

    private void setupVoiceHeartbeat(long interval) {
        log.debug("Starting voice heartbeat...");

        if (voiceHeartbeatScheduler == null) {
            voiceHeartbeatScheduler = Executors.newSingleThreadScheduledExecutor();
        }

        voiceHeartbeatScheduler.scheduleAtFixedRate(this::sendVoiceHeartbeat, interval, interval, TimeUnit.MILLISECONDS);
    }

    private void sendVoiceHeartbeat() {
        log.debug("Sending heartbeat with nonce {}", heartbeatNonce);

        VoiceHeartbeatPayloadData heartbeatData = new VoiceHeartbeatPayloadData();
        heartbeatData.setNonce(heartbeatNonce++);

        VoiceGatewayPayload payload = new VoiceGatewayPayload();
        payload.setEventData(heartbeatData);

        voiceGateway.send(voiceWebSocket, payload);
    }

    //TODO: Create separate GatewayService
    public Request getGatewayRequest() {
        log.debug("Getting gateway request...");
        Call<BotGatewayResponse> gatewayCall = discordService.botGateway();
        String gatewayURL;

        try {
            Response<BotGatewayResponse> response = gatewayCall.execute();

            log.debug(response.toString());

            BotGatewayResponse responseBody = response.body();

            assert responseBody != null;
            gatewayURL = responseBody.getUrl();

            log.debug("Using gateway URL: " + gatewayURL);

            return new Request.Builder()
                    .url(gatewayURL)
                    .build();
        }
        catch (IOException e) {
            log.error("Unable to get gateway URL!");

            e.printStackTrace();

            return null; //TODO: This doesn't seem like the right way to handle this
        }
    }

    private Request getVoiceGatewayRequest(String endpoint) {
        log.debug("Getting voice gateway request using endpoint {}", endpoint);

        return new Request.Builder()
                .url("wss://" + endpoint + "?v=4")
                .build();
    }

    public String getState() {
        return state;
    }
}
