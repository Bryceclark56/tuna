package me.bc56.discord.model.gateway.payload.data;

import com.google.gson.annotations.SerializedName;

public class ConnectionProperties {
    @SerializedName("$os")
    String os;

    @SerializedName("$browser")
    String browser;

    @SerializedName("$device")
    String device;

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }
}
