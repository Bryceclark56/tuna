package me.bc56.discord.model;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

public class Channel {
    String id; //Snowflake

    int type;

    @SerializedName("guild_id")
    String guildId;

    int position;

    //permission_overwrites

    String name; //2-100 characters

    String topic;

    boolean nsfw;

    @SerializedName("last_message_id")
    String lastMessageId;

    int bitrate;

    @SerializedName("user_limit")
    int userLimit;

    @SerializedName("rate_limit_per_user")
    int rateLimitPerUser;

    DiscordUser[] recipients;

    String icon;

    @SerializedName("owner_id")
    String ownerId;

    @SerializedName("application_id")
    String applicationId;

    @SerializedName("parent_id")
    String parentId;

    @SerializedName("last_pin_timestamp")
    Timestamp lastPinTimestamp;
}
