package me.bc56.tuna;

import java.util.UUID;

public abstract class TunaModule {
    final UUID moduleId = UUID.randomUUID();

    protected boolean isRunning = false;

    public abstract void loop();

    public void start() {
        isRunning = true;
    }

    public void stop() {
        isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }
}
