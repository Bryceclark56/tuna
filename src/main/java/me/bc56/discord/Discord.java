package me.bc56.discord;

import me.bc56.discord.accessors.Guilds;
import me.bc56.discord.events.DiscordEventDispatcher;
import me.bc56.discord.gateway.Gateway;
import me.bc56.discord.gateway.GatewayWebsocketListener;
import me.bc56.generic.event.EventDispatcher;
import okhttp3.Request;
import okhttp3.WebSocket;

public class Discord {

    EventDispatcher eventDispatcher = new DiscordEventDispatcher();

    public final Guilds guilds = new Guilds();
    //public final Channels channels;
    //public final Users users;

    private Gateway gateway = new Gateway();

    public void connect(String token) {
        gateway.connect();
    }
}
