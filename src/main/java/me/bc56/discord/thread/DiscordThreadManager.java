package me.bc56.discord.thread;

import me.bc56.generic.thread.Async;
import me.bc56.generic.thread.ThreadManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DiscordThreadManager {
    static ExecutorService executor = Executors.newWorkStealingPool();

    public static void quickRun(Async a) {

    }
}
