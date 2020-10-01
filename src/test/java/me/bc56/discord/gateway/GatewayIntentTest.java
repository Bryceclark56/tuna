package me.bc56.discord.gateway;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static me.bc56.discord.gateway.GatewayIntent.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GatewayIntentTest {

    @Test
    void serializeSet_withSerializedIntent_shouldReturnCorrectEnumSet() {
        int expected = GUILD_MEMBERS.intentValue | GUILD_EMOJIS.intentValue;
        var testEnumSet = EnumSet.of(GUILD_MEMBERS, GUILD_EMOJIS);

        int actual = GatewayIntent.serializeSet(testEnumSet);

        assertEquals(expected, actual);
    }

    @Test
    void serializeSet_withEmptyEnumSet_shouldReturnZero() {
        var testEnumSet = EnumSet.noneOf(GatewayIntent.class);

        int actual = GatewayIntent.serializeSet(testEnumSet);

        assertEquals(0, actual);
    }

    @Test
    void deserializeSet_withValidEnumSet_shouldReturnCorrectSerializedValue() {
        int testSerializedIntent = GUILD_MEMBERS.intentValue | GUILD_EMOJIS.intentValue;
        var expected = EnumSet.of(GUILD_MEMBERS, GUILD_EMOJIS);

        var actual = GatewayIntent.deserializeSet(testSerializedIntent);

        assertEquals(expected, actual);
    }

    @Test
    void deserializeSet_withIntentValueZero_shouldReturnEmptyEnumSet() {
        var expected = EnumSet.noneOf(GatewayIntent.class);

        var actual = GatewayIntent.deserializeSet(0);

        assertEquals(expected, actual);
    }

    @Test
    void deserializeSet_withInvalidIntent_shouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> GatewayIntent.deserializeSet(MAX_INTENT_VALUE + 1));

        assertThrows(IllegalArgumentException.class,
                () -> GatewayIntent.deserializeSet(-1));
    }

    @Test
    void fromValue_withValidIntent_shouldReturnCorrectEnum() {
        int testIntent = GUILD_WEBHOOKS.intentValue;

        var actual = GatewayIntent.fromValue(testIntent);

        assertEquals(GUILD_WEBHOOKS, actual);
    }
}