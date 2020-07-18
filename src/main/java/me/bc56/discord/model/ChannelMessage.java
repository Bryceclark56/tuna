package me.bc56.discord.model;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

public class ChannelMessage {

    String id; //Snowflake

    @SerializedName("channel_id")
    String channelId; //Snowflake

    transient boolean isGuildMessage;

    @SerializedName("guild_id")
    String guildId; //Snowflake

    DiscordUser author;

    GuildMember member;

    String content;

    @SerializedName("timestamp")
    Timestamp creationDate;

    @SerializedName("edited_timestamp")
    Timestamp lastEdited;

    boolean tts;

    boolean mention_everyone;

    //mentions

    //mention_roles

    //mention_channels

    //attachments

    //embeds

    //reactions

    //nonce

    boolean pinned;

    @SerializedName("webhook_id")
    String webhookId; //Snowflake

    int type; //Message type

    //activity

    //application

    //message_reference

    //flags


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public boolean isGuildMessage() {
        return isGuildMessage;
    }

    public void setGuildMessage(boolean guildMessage) {
        isGuildMessage = guildMessage;
    }

    public String getGuildId() {
        return guildId;
    }

    public void setGuildId(String guildId) {
        this.guildId = guildId;
    }

    public DiscordUser getAuthor() {
        return author;
    }

    public void setAuthor(DiscordUser author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public Timestamp getLastEdited() {
        return lastEdited;
    }

    public void setLastEdited(Timestamp lastEdited) {
        this.lastEdited = lastEdited;
    }

    public boolean isTts() {
        return tts;
    }

    public void setTts(boolean tts) {
        this.tts = tts;
    }

    public boolean isMention_everyone() {
        return mention_everyone;
    }

    public void setMention_everyone(boolean mention_everyone) {
        this.mention_everyone = mention_everyone;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public String getWebhookId() {
        return webhookId;
    }

    public void setWebhookId(String webhookId) {
        this.webhookId = webhookId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public GuildMember getMember() {
        return member;
    }

    public void setMember(GuildMember member) {
        this.member = member;
    }
}
