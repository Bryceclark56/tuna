package me.bc56.discord;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public enum GatewayOpcode {
        @SerializedName("0")
        DISPATCH(0),

        @SerializedName("1")
        HEARTBEAT(1),

        @SerializedName("2")
        IDENTIFY(2),

        @SerializedName("3")
        PRESENCE_UPDATE(3),

        @SerializedName("4")
        VOICE_STATE_UPDATE(4),

        @SerializedName("6")
        RESUME(6),

        @SerializedName("7")
        RECONNECT(7),

        @SerializedName("8")
        REQUEST_GUILD_MEMBERS(8),

        @SerializedName("9")
        INVALID_SESSION(9),

        @SerializedName("10")
        HELLO(10),

        @SerializedName("11")
        HEARTBEAT_ACK(11);

        private static final Map<Integer, GatewayOpcode> BY_VALUE = new HashMap<>();

        static {
            for (GatewayOpcode opEnum : values()) {
                BY_VALUE.put(opEnum.opCode, opEnum);
            }
        }

        int opCode;

        GatewayOpcode(int opCode) {
            this.opCode = opCode;
        }

        public int getOpCode() {
            return this.opCode;
        }

        public static GatewayOpcode getAsEnum(int opCode) {
            return BY_VALUE.get(opCode);
        }
    }

    public enum GatewayDispatch {
        READY,
        RESUMED,
        RECONNECT,
        CHANNEL_CREATE,
        CHANNEL_UPDATE,
        CHANNEL_DELETE,
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
        WEBHOOKS_UPDATE;
    }
}
