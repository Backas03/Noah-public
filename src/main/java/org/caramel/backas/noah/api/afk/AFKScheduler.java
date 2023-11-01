package org.caramel.backas.noah.api.afk;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.api.event.user.afk.AFKEndEvent;
import org.caramel.backas.noah.api.event.user.afk.AFKStartEvent;
import org.caramel.backas.noah.api.user.User;

public class AFKScheduler {

    public static final int DEFAULT_TIME_TO_AFK = 60;

    private static AFKScheduler instance;

    public static AFKScheduler getInstance() {
        if (instance == null) instance = new AFKScheduler();
        return instance;
    }

    @Getter
    private int timeToAFK;
    private BukkitTask task;

    public AFKScheduler() {
        timeToAFK = DEFAULT_TIME_TO_AFK;
    }

    public AFKScheduler setTimeToAFK(int second) {
        this.timeToAFK = second;
        for (User user : User.getAll()) {
            user.getAfkCache().setSecond(0);
        }
        return this;
    }

    public void runScheduler() {
        cancelScheduler();
        task = Bukkit.getScheduler().runTaskTimer(Noah.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                User user = User.get(player);
                AFKCache cache = user.getAfkCache();
                Location location = player.getLocation();
                if (cache.getLastLocation() == null) {
                    cache.setLastLocation(location);
                }
                World world = location.getWorld();
                if (world.boundingBoxNoBlocksAround(player.getBoundingBox().expand(0.0625D).expand(0.0D, 0.55D, 0.0D, 0, 0, 0))
                        || player.isSwimming() || player.isFlying() || player.isGliding()
                        || !((Entity) player).isOnGround() || getDistance(cache.getLastLocation(), location) >= 0.5) {
                    int second = cache.getSecond();
                    if (cache.isAFK()) {
                        cache.setSecond(0);
                        Bukkit.getPluginManager().callEvent(new AFKEndEvent(user, second));
                    }
                    cache.setLastLocation(location);
                    cache.setSecond(0);
                    continue;
                }
                cache.setSecond(cache.getSecond() + 1);
                if (cache.getSecond() == timeToAFK) { // start AFK
                    Bukkit.getPluginManager().callEvent(new AFKStartEvent(user));
                }
            }
        }, 0L, 20L);
    }

    public void cancelScheduler() {
        if (task == null) return;
        task.cancel();
        task = null;
    }

    private double getDistance(Location first, Location second) {
        double xd = Math.pow(first.getX() - second.getX(), 2);
        double yd = Math.pow(first.getY() - second.getY(), 2);
        double zd = Math.pow(first.getZ() - second.getZ(), 2);
        return Math.sqrt(xd + yd + zd);
    }

}
