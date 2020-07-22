package me.bc56.tuna;

import com.google.gson.JsonObject;
import me.bc56.discord.DiscordBot;
import me.bc56.discord.audio.AudioTrack;
import me.bc56.discord.factory.DiscordBotFactory;
import me.bc56.discord.model.ChannelMessage;
import me.bc56.discord.model.gateway.event.DispatchEvent;
import me.bc56.discord.model.gateway.event.MessageCreateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class Main {
    static Logger log = LoggerFactory.getLogger(Main.class);
    static final char COMMAND_DELIM = '!';

    public static void main(String[] args) {
        String authToken = System.getenv("DISCORD_AUTH_TOKEN");

        DiscordBot bot = new DiscordBotFactory()
                .authToken(authToken)
                .build();

        log.info("Starting bot");
        bot.start();

        bot.on(MessageCreateEvent.class, (event) -> {
            ChannelMessage message = event.getMessage();
            String content = message.getContent();
            String author = message.getAuthor().getUsername();
            log.debug("Processing channel message in tuna...");

            if (author.equalsIgnoreCase("GenericBot") || !content.startsWith(String.valueOf(COMMAND_DELIM))) {
                return;
            }

            if (content.equalsIgnoreCase("!kys")) {
                log.debug("Attempting to stop bot");
                bot.stop();
                System.exit(0);
            } else if (content.equalsIgnoreCase("!ping")) {
                log.debug("Responding to ping!");
                String channelId = message.getChannelId();
                bot.sendMessage(channelId, "Pong!");
            } else if (content.equalsIgnoreCase("!join")) {
                String guild = message.getGuildId();

                bot.sendMessage(message.getChannelId(), "Connecting to voice channel!");
                bot.connectToVoiceChannel(guild, "99691464426012672");
            } else if (content.equalsIgnoreCase("!play")) {
                // Set up audio provider
                AudioTrack testTrack = new AudioTrack.Builder("test").addOpusJSON().build();
                JankProvider jankProvider = new JankProvider(testTrack);
                bot.registerAudioProvider(jankProvider);
            }
        });

        while (true) ;
    }

    static void handleDispatch(DispatchEvent event) {
        log.debug("Handling dispatch event");
        if (!event.getEventName().equals("MESSAGE_CREATE")) {
            log.debug("It wasn't a message :pensive:");
            return;
        }

        JsonObject data = event.getEventData();

        String content = data.get("content").getAsString();

        log.info("Message created: {}", content);
    }
}
