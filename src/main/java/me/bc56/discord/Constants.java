package me.bc56.discord;

import com.google.gson.annotations.SerializedName;
import me.bc56.discord.gateway.dispatch.type.*;

import java.util.HashMap;
import java.util.Map;

import static me.bc56.discord.Constants.GatewayDispatch.*;
import static me.bc56.discord.Constants.GatewayDispatch.WEBHOOKS_UPDATE;

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
        GUILD_BAN_REMOVE,
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
        WEBHOOKS_UPDATE
    }

    //Some may not be dispatches, but full gateway payload events (Discord docs on it aren't clear on this)
    private static final Map<GatewayDispatch, Class<? extends DispatchData>> dispatchMap = Map.ofEntries(
            Map.entry(READY, Ready.class),
            Map.entry(RESUMED, Resumed.class),
            Map.entry(RECONNECT, Reconnect.class),
            Map.entry(CHANNEL_CREATE, ChannelCreate.class),
            Map.entry(CHANNEL_UPDATE, ChannelUpdate.class),
            Map.entry(CHANNEL_DELETE, ChannelDelete.class),
            Map.entry(CHANNEL_PINS_UPDATE, ChannelPinsUpdate.class),
            Map.entry(GUILD_CREATE, GuildCreate.class),
            Map.entry(GUILD_UPDATE, GuildUpdate.class),
            Map.entry(GUILD_DELETE, GuildDelete.class),
            Map.entry(GUILD_BAN_ADD, GuildBanAdd.class),
            Map.entry(GUILD_BAN_REMOVE, GuildBanRemove.class),
            Map.entry(GUILD_EMOJIS_UPDATE, GuildEmojisUpdate.class),
            Map.entry(GUILD_INTEGRATIONS_UPDATE, GuildIntegrationsUpdate.class),
            Map.entry(GUILD_MEMBER_ADD, GuildMemberAdd.class),
            Map.entry(GUILD_MEMBER_REMOVE, GuildMemberRemove.class),
            Map.entry(GUILD_MEMBER_UPDATE, GuildMemberUpdate.class),
            Map.entry(GUILD_MEMBERS_CHUNK, GuildMembersChunk.class),
            Map.entry(GUILD_ROLE_CREATE, GuildRoleCreate.class),
            Map.entry(GUILD_ROLE_UPDATE, GuildRoleUpdate.class),
            Map.entry(GUILD_ROLE_DELETE, GuildRoleDelete.class),
            Map.entry(INVITE_CREATE, InviteCreate.class),
            Map.entry(INVITE_DELETE, InviteDelete.class),
            Map.entry(MESSAGE_CREATE, MessageCreate.class),
            Map.entry(MESSAGE_UPDATE, MessageUpdate.class),
            Map.entry(MESSAGE_DELETE, MessageDelete.class),
            Map.entry(MESSAGE_DELETE_BULK, MessageDeleteBulk.class),
            Map.entry(MESSAGE_REACTION_ADD, MessageReactionAdd.class),
            Map.entry(MESSAGE_REACTION_REMOVE, MessageReactionRemove.class),
            Map.entry(MESSAGE_REACTION_REMOVE_ALL, MessageReactionRemoveAll.class),
            Map.entry(MESSAGE_REACTION_REMOVE_EMOJI, MessageReactionRemoveEmoji.class),
            Map.entry(PRESENCE_UPDATE, PresenceUpdate.class),
            Map.entry(TYPING_START, TypingStart.class),
            Map.entry(USER_UPDATE, UserUpdate.class),
            Map.entry(VOICE_STATE_UPDATE, VoiceStateUpdate.class),
            Map.entry(VOICE_SERVER_UPDATE, VoiceServerUpdate.class),
            Map.entry(WEBHOOKS_UPDATE, WebhooksUpdate.class)
    );
}
