package org.caramel.backas.noah.api.game;

import lombok.Getter;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.caramel.backas.noah.Noah;

public class GameMap {

    @Getter
    private final String name;
    @Getter
    private final String worldName;

    protected GameMap(String mapName, String worldName) {
        this.name = mapName;
        this.worldName = worldName;
    }


    public void loadMap() {
        if (Bukkit.getServer().getWorld(worldName) == null) {
            World world = new WorldCreator(worldName).type(WorldType.FLAT).environment(World.Environment.NORMAL)
                    .createWorld();
            if (world != null) {
                world.setTime(1000);
                world.setGameRule(GameRule.REDUCED_DEBUG_INFO, true);
                world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
                world.setGameRule(GameRule.DO_TILE_DROPS, false);
                world.setGameRule(GameRule.DO_ENTITY_DROPS, false);
                world.setGameRule(GameRule.DO_FIRE_TICK, false);
                world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
                world.setStorm(false);
                world.setAutoSave(false);
                world.setDifficulty(Difficulty.PEACEFUL);
                Bukkit.getScheduler().runTaskLater(Noah.getInstance(), () -> {
                    for (Entity entity : world.getEntities()) {
                        if (!(entity instanceof Player)) {
                            entity.remove();
                        }
                    }
                }, 1);
            }
        }
    }

    public void unloadMap() {
        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            for (Player player : world.getPlayers()) {
                player.teleport(Noah.getLobby().getSpawn());
            }
            for (Entity entity : world.getEntities()) {
                if (!(entity instanceof Player)) {
                    entity.remove();
                }
            }
        }
        Bukkit.getServer().unloadWorld(worldName, true);
    }

}
