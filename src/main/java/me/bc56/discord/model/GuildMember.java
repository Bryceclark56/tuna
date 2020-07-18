package me.bc56.discord.model;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

public class GuildMember {
    DiscordUser user;

    String nick;

    String[] roles;

    @SerializedName("joined_at")
    Timestamp joinedAt;

    @SerializedName("premium_since")
    Timestamp premium_since;

    boolean deaf;

    boolean mute;
}
