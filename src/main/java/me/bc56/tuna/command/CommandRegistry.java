package me.bc56.tuna.command;

import me.bc56.discord.DiscordBot;
import me.bc56.tuna.model.CommandCircumstances;
import me.bc56.tuna.model.ParsedCommandString;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {
    private static Logger log = LoggerFactory.getLogger(CommandRegistry.class);

    private static Map<String, Class<? extends Command>> registeredCommands = new HashMap<>();

    public static void register(String commandName, Class<? extends Command> command) {
        registeredCommands.put(commandName, command);
    }

    public static void register(Map<String, Class<? extends Command>> commands) {
        commands.forEach(CommandRegistry::register);
    }

    public static void runCommand(DiscordBot bot, CommandCircumstances commandInformation) {
        var parsedCommand = commandInformation.parsedCommand;
        var commandName = parsedCommand.commandName;

        Class<? extends Command> commandClass = registeredCommands.get(commandName);

        try {
            var command = commandClass.getDeclaredConstructor().newInstance();
            command.run(bot, commandInformation);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error("Problem while instantiating new command {}", commandName, e);
        }
    }
}
