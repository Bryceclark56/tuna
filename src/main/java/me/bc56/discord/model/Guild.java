package me.bc56.discord.model;

import com.google.gson.annotations.SerializedName;

import java.sql.Time;
import java.sql.Timestamp;

//https://discord.com/developers/docs/resources/guild#guild-object
public class Guild {
    String id; //Snowflake

    String name; //Note: 2-100 characters

    String icon;

    String splash;

    String discovery_splash;

    boolean owner; //true if the user is the owner of the guild

    @SerializedName("owner_id")
    String ownerId;

    int permissions;

    String region;

    @SerializedName("afk_channel_id")
    String afkChannelId;

    @SerializedName("afk_timeout")
    int afkTimeout;

    //embed_enabled

    //embed_channel_id

    @SerializedName("verification_level")
    int verificationLevel;

    @SerializedName("default_message_notifications")
    int defaultMessageNotifications;

    @SerializedName("explicit_content_filter")
    int explicitContentFilter;

    //roles

    //emojis

    //features

    @SerializedName("mfa_level")
    int mfaLevel;

    @SerializedName("application_id")
    String applicationId;

    @SerializedName("widget_enabled")
    boolean widgetEnabled;

    @SerializedName("widget_channel_id")
    String widgetChannelId;

    @SerializedName("system_channel_id")
    String systemChannelId;

    @SerializedName("system_channel_flags")
    int systemChannelFlags;

    @SerializedName("rules_channel_id")
    String rulesChannelId;

    @SerializedName("joined_at")
    Timestamp joinedAt;

    boolean large;

    boolean unavailable;

    @SerializedName("member_count")
    int memberCount;

    //voice_states

    GuildMember[] members;

    Channel[] channels;

    //presences

    @SerializedName("max_presences")
    int maxPresences;

    @SerializedName("max_members")
    int maxMembers;

    @SerializedName("vanity_url_code")
    String vanityUrlCode;

    String description;

    String banner;

    @SerializedName("premium_tier")
    int premiumTier;

    @SerializedName("premium_subscription_count")
    int premiumSubscriptionCount;

    @SerializedName("preferred_locale")
    String preferredLocale;

    @SerializedName("public_updates_channel_id")
    String publicUpdatesChannelId;

    @SerializedName("max_video_channel_users")
    int maxVideoChannelUsers;

    @SerializedName("approximate_member_count")
    int approximateMemberCount;

    @SerializedName("approximate_presence_count")
    int approximatePresenceCount;
}
