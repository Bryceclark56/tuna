package me.bc56.discord.gateway;

import java.util.Arrays;
import java.util.EnumSet;

// https://discord.com/developers/docs/topics/gateway#gateway-intents
public enum GatewayIntent {
    GUILDS(1),
    GUILD_MEMBERS(1 << 1),
    GUILD_BANS(1 << 2),
    GUILD_EMOJIS(1 << 3),
    GUILD_INTEGRATIONS(1 << 4),
    GUILD_WEBHOOKS(1 << 5),
    GUILD_INVITES(1 << 6),
    GUILD_VOICE_STATES(1 << 7),
    GUILD_PRESENCES(1 << 8),
    GUILD_MESSAGES(1 << 9),
    GUILD_MESSAGE_REACTIONS(1 << 10),
    GUILD_MESSAGE_TYPING(1 << 11),
    DIRECT_MESSAGES(1 << 12),
    DIRECT_MESSAGE_REACTIONS(1 << 13),
    DIRECT_MESSAGE_TYPING(1 << 14);

    public static final int MAX_BIT_LENGTH = 14;
    public static final int MAX_INTENT_VALUE = (1 << (MAX_BIT_LENGTH + 1) ) - 1;

    int intentValue;

    GatewayIntent(int intentValue) {
        this.intentValue = intentValue;
    }

    public static int serializeSet(EnumSet<GatewayIntent> intents) {
        return intents.parallelStream().mapToInt(intent -> intent.intentValue).sum();
    }

    public static EnumSet<GatewayIntent> deserializeSet(int intents) {
        EnumSet<GatewayIntent> intentSet = EnumSet.noneOf(GatewayIntent.class);

        if (intents == 0) {
            return intentSet;
        }

        if (intents > MAX_INTENT_VALUE || intents < 0) {
            throw new IllegalArgumentException("Intent value outside accepted range");
        }

        //Check each bit in the input
        for (int i = 1, value; i <= MAX_INTENT_VALUE; i <<= 1) {
            value = intents & i;

            if (value != 0) {
                intentSet.add(fromValue(value));
            }
        }

        return intentSet;
    }

    public static GatewayIntent fromValue(int intentVal) {
        return Arrays.stream(values()).filter(intent -> intent.intentValue == intentVal).findFirst().orElseThrow();
    }
}
