package me.bc56.discord.gateway.dispatch.type;

import com.google.gson.annotations.SerializedName;

public class Ready extends DispatchData {
    @SerializedName("v")
    int version;


}
