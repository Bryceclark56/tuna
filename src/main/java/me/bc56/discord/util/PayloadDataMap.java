package me.bc56.discord.util;

import me.bc56.discord.model.gateway.payload.data.GatewayPayloadData;
import me.bc56.discord.model.voicegateway.payload.data.VoiceGatewayPayloadData;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

//Map of opcodes to their PayloadData classes
public class PayloadDataMap {
    static Logger log = LoggerFactory.getLogger(PayloadDataMap.class);

    private static Map<Integer, Class<? extends GatewayPayloadData>> map;

    private static Map<Integer, Class<? extends VoiceGatewayPayloadData>> voiceMap;

    public static Map<Integer, Class<? extends GatewayPayloadData>> getMap() {
        if (map == null) {
            log.debug("Creating Payload data map...");

            map = new HashMap<>();

            Reflections reflections = new Reflections(GatewayPayloadData.class.getPackage());
            Set<Class<? extends GatewayPayloadData>> classes = reflections.getSubTypesOf(GatewayPayloadData.class);

            for (Class<? extends GatewayPayloadData> payloadType : classes) {
                try {
                    map.put(payloadType.newInstance().getOpCode(), payloadType);
                    log.debug("Created Payload data map!");
                }
                catch (InstantiationException | IllegalAccessException e) {
                    // If this happens, then just give up
                    log.error("Unable to create payload map", e);
                }
            }
        }

        return map;
    }

    public static Map<Integer, Class<? extends VoiceGatewayPayloadData>> getVoiceMap() {
        if (voiceMap == null) {
            log.debug("Creating Voice Payload data map...");

            voiceMap = new HashMap<>();

            Reflections reflections = new Reflections(VoiceGatewayPayloadData.class.getPackage());
            Set<Class<? extends VoiceGatewayPayloadData>> classes = reflections.getSubTypesOf(VoiceGatewayPayloadData.class);

            for (Class<? extends VoiceGatewayPayloadData> payloadType : classes) {
                try {
                    voiceMap.put(payloadType.newInstance().getOpCode(), payloadType);
                    log.debug("Created Voice Payload data map!");
                }
                catch (InstantiationException | IllegalAccessException e) {
                    // If this happens, then just give up
                    log.error("Unable to create voice payload map", e);
                }
            }
        }

        return voiceMap;
    }
}
