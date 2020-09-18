package me.bc56.discord.util;

import me.bc56.discord.model.gateway.payload.data.*;
import me.bc56.discord.model.voicegateway.payload.data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

//Map of opcodes to their PayloadData classes
public class PayloadDataMap {
    static Logger log = LoggerFactory.getLogger(PayloadDataMap.class);

    private static Map<Integer, Class<? extends GatewayPayloadData>> map;

    private static Map<Integer, Class<? extends VoiceGatewayPayloadData>> voiceMap;

    public static Map<Integer, Class<? extends GatewayPayloadData>> getMap() {
        if (map == null) {
            log.debug("Creating Payload data map...");

            map = new HashMap<>();

            // Register payload classes
            map.put(Constants.GatewayOpcodes.DISPATCH, DispatchPayloadData.class);
            map.put(Constants.GatewayOpcodes.HEARTBEAT, HeartbeatPayloadData.class);
            map.put(Constants.GatewayOpcodes.IDENTIFY, IdentifyPayloadData.class);
            //map.put(Constants.GatewayPayloadType.PRESENCE_UPDATE, PresencePayloadData.class); Unused for now
            map.put(Constants.GatewayOpcodes.VOICE_STATE_UPDATE, VoiceStateUpdatePayloadData.class);
            //map.put(Constants.GatewayPayloadType.RESUME, ResumePayloadData.class); Unused for now
            //map.put(Constants.GatewayPayloadType.RECONNECT, ReconnectPayloadData.class); Unused for now
            //map.put(Constants.GatewayPayloadType.REQUEST_GUILD_MEMBERS, RequestGuildMemberPayloadData.class); Unused for now
            map.put(Constants.GatewayOpcodes.INVALID_SESSION, InvalidSessionPayloadData.class);
            map.put(Constants.GatewayOpcodes.HELLO, HelloPayloadData.class);
            map.put(Constants.GatewayOpcodes.HEARTBEAT_ACK, HeartbeatAckPayloadData.class);
        }

        return map;
    }

    public static Map<Integer, Class<? extends VoiceGatewayPayloadData>> getVoiceMap() {
        if (voiceMap == null) {
            log.debug("Creating Voice Payload data map...");

            voiceMap = new HashMap<>();

            // Register voice payload classes
            voiceMap.put(Constants.VoiceOpcodes.IDENTIFY, VoiceIdentifyPayloadData.class);
            voiceMap.put(Constants.VoiceOpcodes.SELECT_PROTOCOL, VoiceSelectProtocolPayloadData.class);
            voiceMap.put(Constants.VoiceOpcodes.READY, VoiceReadyPayloadData.class);
            voiceMap.put(Constants.VoiceOpcodes.HEARTBEAT, VoiceHeartbeatPayloadData.class);
            voiceMap.put(Constants.VoiceOpcodes.SESSION_DESCRIPTION, VoiceSessionDescriptionPayloadData.class);
            voiceMap.put(Constants.VoiceOpcodes.SPEAKING, VoiceSpeakingPayloadData.class);
            voiceMap.put(Constants.VoiceOpcodes.HEARTBEAT_ACK, VoiceHeartbeatAckPayloadData.class);
            //voiceMap.put(Constants.VoiceGatewayPayloadType.RESUME, );
            voiceMap.put(Constants.VoiceOpcodes.HELLO, VoiceHelloPayloadData.class);
            //voiceMap.put(Constants.VoiceGatewayPayloadType.RESUMED, );
            //voiceMap.put(Constants.VoiceGatewayPayloadType.CLIENT_DISCONNECT, );
        }

        return voiceMap;
    }
}
