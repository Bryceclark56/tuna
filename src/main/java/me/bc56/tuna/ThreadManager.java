package me.bc56.tuna;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.*;

public class ThreadManager {
    private static Logger log = LoggerFactory.getLogger(ThreadManager.class);

    private final ExecutorService genericExecutor;
    private final Map<TunaModule, ExecutorService> dedicatedExecutors;

    public ThreadManager() {
        genericExecutor = Executors.newCachedThreadPool();
        dedicatedExecutors = new ConcurrentHashMap<>(4);
    }

    public void runCoreModule(TunaModule module) {
        log.debug("Running core module {}", module.getClass().getTypeName());

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
            Thread.currentThread().setName("CoreModule-" + module.getClass().getSimpleName());
            while(module.isRunning()) {
                module.loop();
            }
        });
    }

    public boolean stopCoreModule(TunaModule module) {
        log.debug("Stopping core module {}", module.getClass().getTypeName());

        var executor = dedicatedExecutors.get(module);

        if (executor == null) {
            return false;
        }

        module.stop();
        executor.shutdown();
        return true;
    }

    //Generic task execution on general thread pool
    public Future<?> execute(Runnable task) {
        return genericExecutor.submit(task);
    }

    public void unregisterCoreModule(TunaModule module) {
        dedicatedExecutors.remove(module);
    }
}
