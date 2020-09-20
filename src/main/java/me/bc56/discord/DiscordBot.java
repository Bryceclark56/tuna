package me.bc56.discord;

import me.bc56.discord.api.Channels;
import me.bc56.discord.api.Guilds;
import me.bc56.discord.api.Users;
import me.bc56.discord.audio.AudioProvider;
import me.bc56.discord.model.ChannelMessage;
import me.bc56.discord.model.DiscordUser;
import me.bc56.discord.model.Guild;
import me.bc56.discord.model.VoiceState;
import me.bc56.discord.model.api.request.ChannelMessageRequest;
import me.bc56.discord.model.gateway.event.*;
import me.bc56.discord.model.gateway.payload.GatewayPayload;
import me.bc56.discord.model.gateway.payload.data.ConnectionProperties;
import me.bc56.discord.model.gateway.payload.data.IdentifyPayloadData;
import me.bc56.discord.model.api.response.BotGatewayResponse;

import me.bc56.discord.model.gateway.payload.data.VoiceStateUpdatePayloadData;
import me.bc56.discord.model.voicegateway.event.VoiceConnectedEvent;
import me.bc56.discord.model.voicegateway.event.VoiceHelloEvent;
import me.bc56.discord.model.voicegateway.event.VoiceReadyEvent;
import me.bc56.discord.model.voicegateway.event.VoiceSessionDescriptionEvent;
import me.bc56.discord.model.voicegateway.payload.VoiceGatewayPayload;
import me.bc56.discord.model.voicegateway.payload.data.VoiceHeartbeatPayloadData;
import me.bc56.discord.model.voicegateway.payload.data.VoiceIdentifyPayloadData;
import me.bc56.discord.model.voicegateway.payload.data.VoiceSelectProtocolPayloadData;
import me.bc56.discord.model.voicegateway.payload.data.VoiceSpeakingPayloadData;
import me.bc56.discord.service.DiscordService;
import me.bc56.discord.audio.AudioPacket;
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
import java.net.*;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class DiscordBot {
    static Logger log = LoggerFactory.getLogger(DiscordBot.class);

    //API Accessors
    public Guilds guilds;
    public Users users;
    public Channels channels;

    private String authToken;
    private String userAgent;

    private DiscordUser currentUser;

    private Retrofit retrofit;
    private DiscordService discordService;

    private int state = Constants.BotState.STOPPED;

    private WebSocket webSocket;
    private WebSocket voiceWebSocket;

    private DiscordGatewayListener gateway;
    private VoiceGatewayListener voiceGateway;

    private DatagramSocket voiceSocket;
    private InetSocketAddress address;
    private AudioProvider audioProvider;
    private boolean speaking;
    private ScheduledFuture<?> voiceUDPFuture;
    private int silenceCount = 0;

    private EventsManager eventEmitters;

    private ScheduledExecutorService voiceHeartbeatScheduler;

    private long heartbeatNonce;

    private short voiceSequence;
    private Byte[] secretKey;

    public DiscordBot(String authToken, String userAgent, Retrofit retrofit) {
        this.authToken = authToken;
        this.userAgent = userAgent;
        this.retrofit = retrofit;

        this.discordService = this.retrofit.create(DiscordService.class);

        this.heartbeatNonce = 0;

        eventEmitters = EventsManager.getInstance();

        log.debug("Initializing gateway listener...");
        gateway = new DiscordGatewayListener(authToken);

        this.speaking = false;

        registerInternalEvents();

        this.guilds = new Guilds(discordService);
        this.users = new Users(discordService);
        this.channels = new Channels(discordService);
    }

    public void start() {
        state = Constants.BotState.STARTING;

        log.debug("Opening websocket...");
        webSocket = new OkHttpClient().newWebSocket(getGatewayRequest(), gateway);

        this.state = Constants.BotState.RUNNING;
    }

    public void stop() {
        log.debug("Setting bot state to STOPPED");

        state = Constants.BotState.STOPPING;
        webSocket.close(1000, "Stopping");

        state = Constants.BotState.STOPPED;
    }

    public boolean isStopped() {
        return state == Constants.BotState.STOPPED;
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
        eventEmitters.register(VoiceHelloEvent.class, event -> setupVoiceHeartbeat(event.getHeartbeatInterval()));
        eventEmitters.register(VoiceSessionDescriptionEvent.class, event -> secretKey = event.getSecretKey());

        eventEmitters.register(VoiceReadyEvent.class, event -> {
            //Send Select Protocol event to Discord then open a UDP socket for voice
            VoiceSelectProtocolPayloadData voiceSelectProtocolPayloadData = new VoiceSelectProtocolPayloadData();
            voiceSelectProtocolPayloadData.setProtocol("udp");

            VoiceSelectProtocolPayloadData.Data voiceSelectProtocolPayloadDataData = new VoiceSelectProtocolPayloadData.Data();
            voiceSelectProtocolPayloadDataData.setAddress("108.49.53.188");
            voiceSelectProtocolPayloadDataData.setPort(6649);
            voiceSelectProtocolPayloadDataData.setMode("xsalsa20_poly1305");

            voiceSelectProtocolPayloadData.setData(voiceSelectProtocolPayloadDataData);

            VoiceGatewayPayload voiceGatewayPayload = new VoiceGatewayPayload();
            voiceGatewayPayload.setOpCode(Constants.VoiceOpcodes.SELECT_PROTOCOL);
            voiceGatewayPayload.setEventData(voiceSelectProtocolPayloadData);

            voiceGateway.send(voiceWebSocket, voiceGatewayPayload);

            eventEmitters.waitFor(VoiceSessionDescriptionEvent.class);
            initUDPFuture(event.getIp(), event.getPort(), event.getSsrc(), secretKey);
        });
    }

    private void initUDPFuture(String ip, int port, int ssrc, Byte[] secretKey) {
        log.debug("Opening UDP socket...");
        try {
            voiceSocket = new DatagramSocket();
        } catch (Exception e) {
            log.error("Failed to initialize UDP thread for voice: ", e);
            return;
        }
        address = new InetSocketAddress(ip, port);

        eventEmitters.emit(new VoiceConnectedEvent());
        Runnable task = () -> {
            if ((audioProvider != null)) {
                short frame = audioProvider.getFramePos();
                int timestamp = 960 * ((int) frame);

                byte[] primSecretKey = new byte[secretKey.length];
                for (int i = 0; i < secretKey.length; i++) {
                    primSecretKey[i] = secretKey[i];
                }

                if (audioProvider.canProvideFrame()) {
                    if (!speaking) {
                        sendSpeaking(0, 1, ssrc);
                        speaking = true;
                    }

                    byte[] audio = audioProvider.provideFrame();

                    AudioPacket packet = new AudioPacket(frame, timestamp, ssrc, audio, primSecretKey);
                    sendVoiceAudio(packet.getEncryptedPacket());
                } else if (speaking) { // Speaking but can't provide a frame!
                    if (silenceCount < 5) {
                        AudioPacket packet = new AudioPacket(frame, timestamp, ssrc, new byte[] {(byte) 0xF8, (byte) 0xFF, (byte) 0xFE}, primSecretKey);
                        sendVoiceAudio(packet.getEncryptedPacket());
                        silenceCount++;
                    } else if (silenceCount == 5) {
                        sendSpeaking(0, 0, ssrc);
                        speaking = false;
                        silenceCount = 0;
                    }
                }
            } else {
                log.info("I'm a little teapot");
            }
        };

        var scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> new Thread(runnable, "Voice-UDP"));
        voiceUDPFuture = scheduler.scheduleAtFixedRate(task, 0, 19, TimeUnit.MILLISECONDS);
    }

    private void killUDPFuture() {
        if (voiceUDPFuture != null) {
            voiceUDPFuture.cancel(true);
            voiceUDPFuture = null;
        }
    }

    private void sendSpeaking(int delay, int speaking, int ssrc) {
        VoiceSpeakingPayloadData voiceSpeakingPayloadData = new VoiceSpeakingPayloadData();
        voiceSpeakingPayloadData.setDelay(delay);
        voiceSpeakingPayloadData.setSsrc(ssrc);
        voiceSpeakingPayloadData.setSpeaking(speaking);

        VoiceGatewayPayload payload = new VoiceGatewayPayload();
        payload.setOpCode(Constants.VoiceOpcodes.SPEAKING);
        payload.setEventData(voiceSpeakingPayloadData);

        voiceGateway.send(voiceWebSocket, payload);
    }

    public void sendVoiceAudio(byte[] bytes) {
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address);

        try {
            voiceSocket.send(packet);
        } catch (Exception e) {
            log.error("Failed to send audio packet: ", e);
        }
    }

    public void disconnectFromVoice(String guildId) {
        if (voiceSocket == null || voiceWebSocket == null) {
            return;
        }

        VoiceStateUpdatePayloadData voiceStateUpdate = new VoiceStateUpdatePayloadData();
        voiceStateUpdate.setGuildId(guildId);
        voiceStateUpdate.setChannelId(null);
        voiceStateUpdate.setSelfMute(false);
        voiceStateUpdate.setSelfDeaf(false);

        GatewayPayload payload = new GatewayPayload();
        payload.setOpCode(Constants.GatewayOpcodes.VOICE_STATE_UPDATE);
        payload.setEventData(voiceStateUpdate);

        gateway.send(webSocket, payload);

        killUDPFuture();

        voiceSocket.close();
        voiceSocket = null;

        voiceWebSocket.close(4014, "Client closed voice websocket");
        voiceWebSocket = null;

        voiceSequence = 0;
    }

    public void connectToVoiceChannel(String guildId, String channelId) {
        log.debug("Attempting to connect to voice channel {} in guild {}", channelId, guildId);
        voiceGateway = new VoiceGatewayListener();

        VoiceStateUpdatePayloadData voiceStateUpdatePayloadData = new VoiceStateUpdatePayloadData();
        voiceStateUpdatePayloadData.setGuildId(guildId);
        voiceStateUpdatePayloadData.setChannelId(channelId);
        voiceStateUpdatePayloadData.setSelfDeaf(false);
        voiceStateUpdatePayloadData.setSelfMute(false);

        GatewayPayload payload = new GatewayPayload();
        payload.setOpCode(Constants.GatewayOpcodes.VOICE_STATE_UPDATE);
        payload.setEventData(voiceStateUpdatePayloadData);

        var voiceServerFuture = eventEmitters.register(VoiceServerUpdateEvent.class);
        var voiceStateFuture = eventEmitters.register(VoiceStateUpdateEvent.class);

        log.debug("Sending status update payload for voice then waiting...");
        gateway.send(webSocket, payload);

        VoiceServerUpdateEvent voiceServerEvent;
        VoiceStateUpdateEvent voiceStateEvent;

        try {
            CompletableFuture.allOf(voiceServerFuture, voiceStateFuture).get(10, TimeUnit.SECONDS);

            voiceServerEvent = voiceServerFuture.get();
            voiceStateEvent = voiceStateFuture.get();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            //TODO: Should we fire an event here?
            log.error("Error in connecting to voice", e);
            return;
        }

        log.debug("Connecting to voice gateway at {}", voiceServerEvent.getEndpoint());
        voiceWebSocket = new OkHttpClient().newWebSocket(getVoiceGatewayRequest(voiceServerEvent.getEndpoint()), voiceGateway);

        VoiceState voiceState = voiceStateEvent.getVoiceState();

        VoiceIdentifyPayloadData identifyPayloadData = new VoiceIdentifyPayloadData();
        identifyPayloadData.setServerId(voiceState.getGuildId()); //Does server == guild??
        identifyPayloadData.setSessionId(voiceState.getSessionId());
        identifyPayloadData.setUserId(voiceState.getUserId());
        identifyPayloadData.setToken(voiceServerEvent.getToken());

        VoiceGatewayPayload voiceGatewayPayload = new VoiceGatewayPayload();
        voiceGatewayPayload.setOpCode(Constants.VoiceOpcodes.IDENTIFY);
        voiceGatewayPayload.setEventData(identifyPayloadData);

        log.debug("Sending identify to voice gateway");

        voiceGateway.send(voiceWebSocket, voiceGatewayPayload);
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
            case "MESSAGE_CREATE" -> {
                MessageCreateEvent msgEvent = new MessageCreateEvent(event);
                ChannelMessage msg = msgEvent.getMessage();
                log.debug("New message from channel {} by user {} that says: \n{}\nEND_OF_MESSAGE",
                        msg.getChannelId(), msg.getAuthor().getUsername(), msg.getContent());
                eventEmitters.emit(msgEvent);
            }
            case "READY" -> {
                ReadyEvent readyEvent = new ReadyEvent(event);
                eventEmitters.emit(readyEvent);
            }
            case "VOICE_SERVER_UPDATE" -> {
                VoiceServerUpdateEvent voiceServerUpdate = new VoiceServerUpdateEvent(event);
                eventEmitters.emit(voiceServerUpdate);
            }
            case "VOICE_STATE_UPDATE" -> {
                VoiceStateUpdateEvent voiceStateUpdateEvent = new VoiceStateUpdateEvent(event);
                eventEmitters.emit(voiceStateUpdateEvent);
            }
        }

        log.debug("Finished processing dispatch");
    }

    //Called by external clients to register events
    public <E extends GatewayEvent> void on(Class<E> event, Consumer<E> callback) {
        eventEmitters.register(event, callback);
    }

    private int rate = 0;
    private long rateHit = 0;
    public void sendMessage(String channel, String content) {
        ChannelMessageRequest request = new ChannelMessageRequest();
        request.setContent(content);

        if (rate >= 10) {
            if (System.currentTimeMillis() - rateHit >= 10000) {
                rate = 0;
            }
            else {
                log.error("Unable to send message, limit hit");
                return;
            }
        }

        log.debug("Sending message to Discord channel {} with content \"{}\"", channel, content);
        Call<ChannelMessage> messageCall = discordService.sendMessage(channel, request);

        messageCall.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NotNull Call<ChannelMessage> call, @NotNull Response<ChannelMessage> response) {
                log.debug("Successfully sent message!");
            }

            @Override
            public void onFailure(@NotNull Call<ChannelMessage> call, @NotNull Throwable t) {
                log.debug("Failed to send message!");
            }
        });

        if (++rate >= 10) {
            rateHit = System.currentTimeMillis();
            log.error("Limit reached for sending messages");
        }
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
        Thread.currentThread().setName("VoiceGateway-Heartbeat");
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
        Call<BotGatewayResponse> gatewayCall;
        String gatewayURL = "";

        try {
            boolean shouldRetry = false;
            BotGatewayResponse responseBody;
            do {
                gatewayCall = discordService.botGateway();
                Response<BotGatewayResponse> response = gatewayCall.execute();

                log.debug(response.toString());

                responseBody = response.body();

                if (response.code() != 200) {
                    log.error("Problem getting Discord gateway (code was {}): {}", response.code(), response.message());

                    if (response.code() == 429) {
                        long retry = Long.parseLong(Objects.requireNonNull(response.headers().get("retry-after")));
                        log.error("Too many requests (limit is {}), retrying after {}; isGlobal = {}", response.headers().get("X-RateLimit-Limit"), retry, response.headers().get("X-RateLimit-Global"));
                        try {
                            shouldRetry = true;
                            Thread.sleep(retry + 10000);
                            log.error("Retrying...");
                        } catch (InterruptedException e) {
                            log.error("Interrupted while waiting to retry GatewayRequest", e);
                        }
                    }
                }
            } while(shouldRetry);

            if (responseBody != null) {
                gatewayURL = responseBody.getUrl();
            }
            else {
                log.error("Null response body while getting gateway");
                System.exit(1);
            }

            log.debug("Using gateway URL: " + gatewayURL);

            return new Request.Builder()
                    .url(gatewayURL)
                    .build();
        } catch (IOException e) {
            log.error("Unable to get gateway URL!");

            e.printStackTrace();

            return null; //TODO: This doesn't seem like the right way to handle this
        }
    }

    private Request getVoiceGatewayRequest(String endpoint) {
        log.debug("Getting voice gateway request using endpoint {}", endpoint);

        String portless = endpoint.substring(0, endpoint.length() - 3);

        return new Request.Builder()
                .url("wss://" + portless + "/?v=4")
                .build();
    }

    public int getState() {
        return state;
    }

    public <T extends AudioProvider> void registerAudioProvider(T provider) {
        audioProvider = provider;
    }
}
