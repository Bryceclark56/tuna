package me.bc56.tuna;

import java.util.Map;
import java.util.concurrent.*;

public class ThreadManager {
    private final ExecutorService genericExecutor;
    private final Map<TunaModule, ExecutorService> dedicatedExecutors;

    public ThreadManager() {
        genericExecutor = Executors.newFixedThreadPool(10);
        dedicatedExecutors = new ConcurrentHashMap<>(4);
    }

    public void runCoreModule(TunaModule module) {
        ExecutorService executor;

        if (!dedicatedExecutors.containsKey(module)) {
            executor = Executors.newSingleThreadExecutor();
            dedicatedExecutors.put(module, executor);
        }
        else {
            executor = dedicatedExecutors.get(module);
        }

        module.start();
        executor.execute(() -> {
            while(module.isRunning()) {
                module.loop();
            }
        });
    }

    public boolean stopCoreModule(TunaModule module) {
        var executor = dedicatedExecutors.get(module);

        if (executor == null) {
            return false;
        }

        module.stop();
        executor.shutdown();
        return true;
    }

    //Generic task execution on general thread pool
    public void execute(Runnable task) {
        genericExecutor.execute(task);
    }

    public void unregisterCoreModule(TunaModule module) {
        dedicatedExecutors.remove(module);
    }
}
