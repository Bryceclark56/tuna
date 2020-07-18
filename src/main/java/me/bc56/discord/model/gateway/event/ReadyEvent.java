package me.bc56.discord.model.gateway.event;

import com.google.gson.Gson;
import me.bc56.discord.model.DiscordUser;

public class ReadyEvent extends DispatchEvent {
    DiscordUser user;

    public ReadyEvent(DispatchEvent dispatchEvent) {
        super(dispatchEvent.getEventName(), dispatchEvent.getEventData());

        Gson gson = new Gson();
        this.user = gson.fromJson(eventData.get("user"), DiscordUser.class);
    }

    public DiscordUser getUser() {
        return user;
    }
}
