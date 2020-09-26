package me.bc56.discord.gateway.dispatch;

import me.bc56.discord.gateway.dispatch.type.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DispatchHandler {
    public static Logger log = LoggerFactory.getLogger(DispatchHandler.class);

    @SuppressWarnings("unchecked") //We guarantee the superclass is DispatchData in constructMap()
    static Map<Class<? extends DispatchData>, Method> classToMethodMap = (Map<Class<? extends DispatchData>, Method>) constructMap();

    static Map<Class<?>, Method> constructMap() {
        var methods = DispatchHandler.class.getDeclaredMethods();

        //We only want methods of the pattern: handle(Class var)
        return Arrays.stream(methods)
                .filter(method -> method.getName().equals("handle")
                        && method.getParameterCount() == 1
                        && method.getParameterTypes()[0].getSuperclass().equals(DispatchData.class))
                .collect(Collectors.toUnmodifiableMap(
                        method -> method.getParameterTypes()[0],
                        method -> method
                ));
    }

    public DispatchHandler() {
    }

    public <T extends DispatchData> void handleDispatch(T dispatchData) {
        try {
            classToMethodMap.get(dispatchData.getClass()).invoke(this, dispatchData);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Problem while handling dispatch", e);
        }
    }

    void handle(Ready ready) {

    }

    void handle(Resumed resumed) {

    }

    void handle(ChannelCreate channelCreate) {
    }

    void handle(ChannelUpdate channelUpdate) {
    }

    void handle(ChannelDelete channelDelete) {
    }

    void handle(ChannelPinsUpdate channelPinsUpdate) {
    }

    void handle(GuildCreate guildCreate) {
    }

    void handle(GuildUpdate guildUpdate) {
    }

    void handle(GuildDelete guildDelete) {
    }

    void handle(GuildBanAdd guildBanAdd) {
    }

    void handle(GuildBanRemove guildBanRemove) {
    }

    void handle(GuildEmojisUpdate guildEmojisUpdate) {
    }

    void handle(GuildIntegrationsUpdate guildIntegrationsUpdate) {
    }

    void handle(GuildMemberAdd guildMemberAdd) {
    }

    void handle(GuildMemberRemove guildMemberRemove) {
    }

    void handle(GuildMemberUpdate guildMemberUpdate) {
    }

    void handle(GuildMembersChunk guildMembersChunk) {
    }

    void handle(GuildRoleCreate guildRoleCreate) {
    }

    void handle(GuildRoleUpdate guildRoleUpdate) {
    }

    void handle(GuildRoleDelete guildRoleDelete) {
    }

    void handle(InviteCreate inviteCreate) {
    }

    void handle(InviteDelete inviteDelete) {
    }

    void handle(MessageCreate messageCreate) {
    }

    void handle(MessageUpdate messageUpdate) {
    }

    void handle(MessageDelete messageDelete) {
    }

    void handle(MessageDeleteBulk messageDeleteBulk) {
    }

    void handle(MessageReactionAdd messageReactionAdd) {
    }

    void handle(MessageReactionRemove messageReactionRemove) {
    }

    void handle(MessageReactionRemoveAll messageReactionRemoveAll) {
    }

    void handle(MessageReactionRemoveEmoji messageReactionRemoveEmoji) {
    }

    void handle(PresenceUpdate presenceUpdate) {
    }

    void handle(TypingStart typingStart) {
    }

    void handle(UserUpdate userUpdate) {
    }

    void handle(VoiceStateUpdate voiceStateUpdate) {
    }

    void handle(VoiceServerUpdate voiceServerUpdate) {
    }

    void handle(WebhooksUpdate webhooksUpdate) {
    }
}
