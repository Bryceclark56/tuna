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
        var executor = Executors.newSingleThreadExecutor();

        dedicatedExecutors.put(module, Executors.newSingleThreadExecutor());

        module.start();
        executor.execute(() -> {
            while(module.isRunning()) {
                module.loop();
            }
        });
    }

    //Generic task execution on general thread pool
    public <E> FutureTask<E> execute(Callable<E> task) {
        FutureTask<E> taskFuture = new FutureTask<>(task);

        genericExecutor.submit(taskFuture);

        return taskFuture;
    }
}
