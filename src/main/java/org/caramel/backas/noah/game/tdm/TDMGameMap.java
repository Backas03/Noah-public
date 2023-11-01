package org.caramel.backas.noah.game.tdm;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.inventory.ItemStack;
import org.caramel.backas.noah.game.IGameMap;
import org.caramel.backas.noah.game.ITeamType;
import org.caramel.backas.noah.util.Position;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

public class TDMGameMap implements IGameMap {

    private final Map<ITeamType, Position> spawnPoints;

    private final String name;
    private final String worldName;
    private final ItemStack icon;
    private boolean enable;

    protected TDMGameMap(String mapName, String worldName, ItemStack icon, Map<ITeamType, Position> spawnPoints) {
        this.spawnPoints = spawnPoints;
        this.name = mapName;
        this.worldName = worldName;
        this.enable = true;
        this.icon = icon;
    }

    public ItemStack getIcon() {
        return icon;
    }


    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean loadWorld() {
        if (Bukkit.getWorld(worldName) == null) {
            World world = new WorldCreator(worldName).type(WorldType.FLAT)
                            .environment(World.Environment.NORMAL).createWorld();
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
                return true;
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean unloadWorld() {
        final World world = Bukkit.getWorld(worldName);
        if (world != null) {
            Bukkit.getServer().unloadWorld(world, false);
            return true;
        }
        return false;
    }

    @Override
    public String getWorldName() {
        return worldName;
    }

    @Override
    public Component getName() {
        return Component.text(name);
    }

    @Override
    public Location getSpawnPoint(ITeamType type) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            LoggerFactory.getLogger(TDMGameMap.class).warn("cannot found a world!. map={}, worldName={}", name, worldName);
        }
        return spawnPoints.get(type).toLocation(Bukkit.getWorld(worldName));
    }

    @Override
    public boolean isEnabled() {
        return enable;
    }

    @Override
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public String toString() {
        return String.format("TDMGameMap{name=%s, world=%s, enabled=%b, loaded=%b}", name, worldName, enable, Bukkit.getWorld(worldName) != null);
    }

    public static class Builder {

        Builder() {}

        private ItemStack icon;
        private String worldName;
        private final Map<ITeamType, Position> spawnLocation = new HashMap<>();
        private String mapName;

        public Builder setWorldName(String worldName) {
            this.worldName = worldName;
            return this;
        }

        public Builder setIcon(ItemStack icon) {
            this.icon = icon;
            return this;
        }

        public Builder setSpawn(TDMTeam.Type team, Position position) {
            spawnLocation.put(team, position);
            return this;
        }

        public Builder setMapName(String mapName) {
            this.mapName = mapName;
            return this;
        }

        public TDMGameMap build() {
            return new TDMGameMap(mapName, worldName, icon, spawnLocation);
        }
    }
}
