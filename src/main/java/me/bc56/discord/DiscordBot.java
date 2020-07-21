package me.bc56.discord;

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
import me.bc56.discord.model.voicegateway.event.VoiceHelloEvent;
import me.bc56.discord.model.voicegateway.event.VoiceReadyEvent;
import me.bc56.discord.model.voicegateway.event.VoiceSessionDescriptionEvent;
import me.bc56.discord.model.voicegateway.payload.VoiceGatewayPayload;
import me.bc56.discord.model.voicegateway.payload.data.VoiceHeartbeatPayloadData;
import me.bc56.discord.model.voicegateway.payload.data.VoiceIdentifyPayloadData;
import me.bc56.discord.model.voicegateway.payload.data.VoiceSelectProtocolPayloadData;
import me.bc56.discord.model.voicegateway.payload.data.VoiceSpeakingPayloadData;
import me.bc56.discord.service.DiscordService;
import me.bc56.discord.util.AudioPacket;
import me.bc56.discord.util.AudioTrack;
import me.bc56.discord.util.Constants;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import org.eclipse.collections.api.list.primitive.ByteList;
import org.eclipse.collections.impl.factory.primitive.ByteLists;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.time.Instant;
import java.util.concurrent.*;
import java.util.function.Consumer;

import static me.bc56.discord.util.Zoom.b;

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

    private DatagramSocket voiceSocket;
    private InetSocketAddress address;

    private EventsManager eventEmitters;

    private ScheduledExecutorService voiceHeartbeatScheduler;

    private long heartbeatNonce;

    private short voiceSequence;
    private int voiceSSRC;
    private Byte[] secretKey;

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
            voiceGatewayPayload.setOpCode(Constants.VoiceGatewayPayloadType.SELECT_PROTOCOL);
            voiceGatewayPayload.setEventData(voiceSelectProtocolPayloadData);

            voiceGateway.send(voiceWebSocket, voiceGatewayPayload);

            try {
                log.debug("Opening UDP socket...");
                voiceSocket = new DatagramSocket();
                address = new InetSocketAddress(event.getIp(), event.getPort());
                voiceSSRC = event.getSsrc();

                AudioTrack track = new AudioTrack.Builder("Test").addOpusJSON().build();

                byte[] primSecretKey = new byte[secretKey.length];
                for (int i = 0; i < secretKey.length; i++) {
                    primSecretKey[i] = secretKey[i];
                }
                Runnable task = () -> {
                  if (track.canProvideFrame()) {
                      int frame = track.getFramePos();

                      int timestamp = 960 * frame;

                      AudioPacket packet = new AudioPacket((short) frame, timestamp, voiceSSRC, track.nextFrame(), primSecretKey);
                      try {
                          sendVoiceAudio(packet.getEncryptedPacket());
                      } catch (Exception e) {
                          e.printStackTrace();
                      }
                  }
                };

                var scheduler = Executors.newSingleThreadScheduledExecutor();
                scheduler.scheduleAtFixedRate(task, 0, 19, TimeUnit.MILLISECONDS);
            } catch (SocketException e) {
                log.error("Problem with the voice UDP socket!", e);
            }
        });
    }

    public void sendSpeaking(int delay, int speaking) {
        while (voiceSSRC == 0) {
            try {
                Thread.sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        VoiceSpeakingPayloadData voiceSpeakingPayloadData = new VoiceSpeakingPayloadData();
        voiceSpeakingPayloadData.setDelay(delay);
        voiceSpeakingPayloadData.setSsrc(voiceSSRC);
        voiceSpeakingPayloadData.setSpeaking(speaking);

        VoiceGatewayPayload payload = new VoiceGatewayPayload();
        payload.setOpCode(Constants.VoiceGatewayPayloadType.SPEAKING);
        payload.setEventData(voiceSpeakingPayloadData);

        voiceGateway.send(voiceWebSocket, payload);
    }

    public void sendVoiceAudio(byte[] bytes) throws IOException {
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address);
        voiceSocket.send(packet);
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
        payload.setOpCode(Constants.GatewayPayloadType.VOICE_STATE_UPDATE);
        payload.setEventData(voiceStateUpdate);

        gateway.send(webSocket, payload);


        voiceSocket.close();
        voiceSocket = null;

        voiceWebSocket.close(4014, "Client closed voice websocket");
        voiceWebSocket = null;

        voiceSSRC = 0;
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
        payload.setOpCode(Constants.GatewayPayloadType.VOICE_STATE_UPDATE);
        payload.setEventData(voiceStateUpdatePayloadData);

        CompletableFuture<VoiceServerUpdateEvent> voiceServerFuture = new CompletableFuture<>();
        CompletableFuture<VoiceStateUpdateEvent> voiceStateFuture = new CompletableFuture<>();

        eventEmitters.register(VoiceServerUpdateEvent.class, voiceServerFuture::complete);
        eventEmitters.register(VoiceStateUpdateEvent.class, voiceStateFuture::complete);

        log.debug("Sending status update payload for voice then waiting...");
        gateway.send(webSocket, payload);

        VoiceServerUpdateEvent voiceServerEvent;
        VoiceStateUpdateEvent voiceStateEvent;

        try {
            CompletableFuture.allOf(voiceServerFuture, voiceStateFuture).get(10, TimeUnit.SECONDS);

            voiceServerEvent = voiceServerFuture.get();
            voiceStateEvent = voiceStateFuture.get();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
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
        voiceGatewayPayload.setOpCode(Constants.VoiceGatewayPayloadType.IDENTIFY);
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

    public void sendMessage(String channel, String content) {
        ChannelMessageRequest request = new ChannelMessageRequest();
        request.setContent(content);

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

        String portless = endpoint.substring(0, endpoint.length() - 3);

        return new Request.Builder()
                .url("wss://" + portless + "/?v=4")
                .build();
    }

    public String getState() {
        return state;
    }
}
