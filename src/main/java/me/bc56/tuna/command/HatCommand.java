package me.bc56.tuna.command;

import me.bc56.discord.DiscordBot;
import me.bc56.tuna.model.CommandCircumstances;

import java.text.MessageFormat;

public class HatCommand extends Command {

    private static final String[] results = {
            "A rock!",
            "Cozza's Subaru WRX!\nCozza is gonna be angry when he find outs!",
            "An ear of corn!",
            "Silence.",
            "A single grain of sand!",
            "A dragon!",
            "A penguin!",
            "An ice cream cone!",
            "Tero's cat. How'd it get all the way here?",
            //"(A random staff member that is online)",
            "Cheese!",
            "Tero's teacup!",
            "One of Tanner's lolis!",
            "Their soul...",
            "A rabbit!",
            "A fox!",
            "An IBM Model M keyboard!",
            "A framed portrait of Bryce!",
            "A framed portrait of Expert's Arch Linux shot glasses!",
            "Niksa's kebab!",
            "Asdroth's cat!",
            "Genos' pack of cigarettes!",
            "A can of febreze!",
            "A black hole!",
            "Accelleon's guitar!",
            "A very sharp knife!",
            "A very dull knife!\nYou should sharpen that.",
            "A raw chicken breast!",
            "A cookie!",
            "Hydrochloric acid!",
            "An anvil!",
            "A lego brick!",
            "A print-out of the \"Heat Death of the Universe\" wikipedia page!",
            //"A(n) [P].", TODO: Embedded variables
            "A 20-sided die!",
            "A flattened copper coin!",
            "A steam controller!",
            "Genos' fiber optic cable for his internet!",
            "their mom?",
            "their dad?",
            "an Omicronian youngling!\nLrrr is going to eat you for this."
    };

    @Override
    public void run(DiscordBot bot, CommandCircumstances information) {
        var choice = (int) (Math.random() * results.length);

        var channel = information.origin.getChannelId();
        var sender = information.origin.getAuthor().getUsername();

        var output = MessageFormat.format(
                "{0} reaches deep into a hat and pulls out... {1}",
                sender,
                results[choice]);

        bot.sendMessage(channel, output);
    }
}
