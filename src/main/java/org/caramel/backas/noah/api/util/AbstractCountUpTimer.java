package org.caramel.backas.noah.api.util;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.caramel.backas.noah.Noah;

public abstract class AbstractCountUpTimer {

    @Getter
    protected BukkitTask task;
    @Setter @Getter
    private int counts;

    protected abstract void onStart();
    protected abstract void onCount(int counts);
    protected abstract void onStop();
    protected abstract int getStartCount();
    protected abstract int getDelay();
    protected abstract int getPeriod();

    public void start() {
        stop();
        counts = getStartCount();
        onStart();
        task = Bukkit.getScheduler().runTaskTimer(Noah.getInstance(), () -> onCount(counts++), getDelay(), getPeriod());
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            onStop();
        }
        task = null;
    }

    public boolean isRunning() {
        return task != null;
    }

}
