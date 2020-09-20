package me.bc56.discord.util;

import java.time.Instant;

public class Constants {
    public static final String DISCORD_BASE_URL = "https://discord.com/api/";

    public static final Instant DISCORD_EPOCH = Instant.ofEpochMilli(Long.parseLong("1420070400000"));

    public static class BotState {
        public static final int STOPPED  = 0;
        public static final int STARTING = 1;
        public static final int RUNNING  = 2;
        public static final int STOPPING = 3;
    }

    public static class GatewayOpcodes {
        public static final int DISPATCH              =  0;
        public static final int HEARTBEAT             =  1;
        public static final int IDENTIFY              =  2;
        public static final int PRESENCE_UPDATE       =  3;
        public static final int VOICE_STATE_UPDATE    =  4;
        public static final int RESUME                =  6;
        public static final int RECONNECT             =  7;
        public static final int REQUEST_GUILD_MEMBERS =  8;
        public static final int INVALID_SESSION       =  9;
        public static final int HELLO                 = 10;
        public static final int HEARTBEAT_ACK         = 11;
    }

    public static class VoiceOpcodes {
        public static final int IDENTIFY            =  0;
        public static final int SELECT_PROTOCOL     =  1;
        public static final int READY               =  2;
        public static final int HEARTBEAT           =  3;
        public static final int SESSION_DESCRIPTION =  4;
        public static final int SPEAKING            =  5;
        public static final int HEARTBEAT_ACK       =  6;
        public static final int RESUME              =  7;
        public static final int HELLO               =  8;
        public static final int RESUMED             =  9;
        public static final int CLIENT_DISCONNECT   = 13;
    }

    public enum ChannelType {
        GUILD_TEXT(0),
        DM(1),
        GUILD_VOICE(2),
        GROUP_DM(3),
        GUILD_CATEGORY(4),
        GUILD_NEWS(5),
        GUILD_STORE(6);

        public final int typeNumber;

        ChannelType(int typeNumber) {
            this.typeNumber = typeNumber;
        }

        public boolean equals(ChannelType other) {
            return typeNumber == other.typeNumber;
        }

        public static ChannelType valueOfTypeNumber(int typeNumber) {
            for (ChannelType c : values()) {
                if (c.typeNumber == typeNumber) {
                    return c;
                }
            }

            return null;
        }
    }
}
