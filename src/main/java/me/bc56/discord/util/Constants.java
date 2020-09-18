package me.bc56.discord.util;

import java.time.Instant;
import java.util.Map;

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

    public static class ChannelTypes {
        public static final int GUILD_TEXT     = 0;
        public static final int DM             = 1;
        public static final int GUILD_VOICE    = 2;
        public static final int GROUP_DM       = 3;
        public static final int GUILD_CATEGORY = 4;
        public static final int GUILD_NEWS     = 5;
        public static final int GUILD_STORE    = 6;
    }

    /*public static class JsonErrorCodes {
        public static final int GENERAL_ERROR = 0;
        public static final int UNKNOWN_ACCOUNT = 10001;
        public static final int UNKNOWN_APPLICATION = 10002;
        public static final int UNKNOWN_CHANNEL = 10003;
        public static final int UNKNOWN_GUILD = 10004;
        public static final int UNKNOWN_INTEGRATION = 10005;
        public static final int UNKNOWN_INVITE = 10006;
        public static final int UNKNOWN_MEMBER = 10007;
        public static final int UNKNOWN_MESSAGE = 10008;
        public static final int UNKNOWN_PERMISSION_OVERWRITE = 10009;
        public static final int UNKNOWN_PROVIDER = 10010;
        public static final int UNKNOWN_ROLE = 10011;
        public static final int UNKNOWN_TOKEN = 10012;
        public static final int UNKNOWN_USER = 10013;
        public static final int UNKNOWN_EMOJI = 10014;
        public static final int UNKNOWN_WEBHOOK = 10015;
        public static final int UNKNOWN_BAN = 10026;
        public static final int UNKNOWN_SKU = 10027;
        public static final int UNKNOWN_STORE_LISTING = 10028;
        public static final int UNKNOWN_ENTITLEMENT = 10029;
        public static final int UNKNOWN_BUILD = 10030;
        public static final int UNKNOWN_LOBBY = 10031;
        public static final int UNKNOWN_BRANCH = 10032;
        public static final int UNKNOWN_REDISTRIBUTABLE = 10036;
        public static final int NO_BOTS_ENDPOINT = 20001;
        public static final int BOTS_ONLY_ENDPOINT = 20002;
        public static final int CHANNEL_WRITE_RATE_LIMIT = 20028;
        public static final int MAXIMUM_GUILDS = 30001;
        public static final int MAXIMUM_FRIENDS = 30002;
        public static final int MAXIMUM_CHANNEL_PINS = 30003;
        public static final int MAXIMUM_GUILD_ROLES = 30005;
        public static final int MAXIMUM_WEBHOOKS = 30007;
        public static final int MAXIMUM_REACTIONS = 30010;
        public static final int MAXIMUM_GUILD_CHANNELS = 30013;
        public static final int MAXIMUM_MESSAGE_ATTACHMENTS = 30015;
        public static final int MAXIMUM_INVITES = 30016;
        public static final int UNAUTHORIZED = 40001;
        public static final int MUST_VERIFY = 40002;
        public static final int REQUEST_TOO_LARGE = 40005;
        public static final int FEATURE_TEMP_DISABLED = 40006;
        public static final int USER_BANNED = 40007;
        public static final int MESSAGE_ALREADY_CROSSPOSTED = 40033;
        public static final int MISSING_ACCESS = 50001;
        public static final int INVALID_ACCOUNT_TYPE = 50002;
        public static final int CANNOT_EXECUTE_IN_DM = 50003;
        public static final int GUILD_WIDGET_DISABLED = 50004;
        public static final int CANNOT_EDIT_MESSAGE_OF_OTHER_USER = 50005;
        public static final int CANNOT_SEND_EMPTY_MESSAGE = 50006;
        public static final int CANNOT_SEND_MESSAGES_TO_USER = 50007;
        public static final int CANNOT_SEND_MESSAGES_IN_VOICE = 50008;
        public static final int CHANNEL_VERIFICATION_TOO_HIGH = 50009;

    }*/

    /*public static Map<Integer, String> JsonErrorCodeStrings = Map.of(
                0, "General error (such as a malformed request body, amongst other things)",
            10001, "Unknown account",
            10002, "Unknown application",
            10003, "Unknown channel",
            10004, "Unknown guild",
            10005, "Unknown integration",
            10006, "Unknown invite",
            10007, "Unknown member",
            10008, "Unknown message",
            10009, "Unknown permission overwrite",
            10010, "Unknown provider",
            10011, "Unknown role",
            10012, "Unknown token",
            10013, "Unknown emoji",
            10015, "Unknown webhook",
            10026, "Unknown ban",
            10027, "Unknown SKU",
            10028, "Unknown Store Listing",
            10029, "Unknown entitlement",
            10030, "Unknown build",
            10031, "Unknown lobby",
            10032, "unknown branch",

    );*/
}
