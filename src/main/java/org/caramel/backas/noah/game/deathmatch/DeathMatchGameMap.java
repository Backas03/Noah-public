package org.caramel.backas.noah.game.deathmatch;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.caramel.backas.noah.api.game.GameMap;
import org.caramel.backas.noah.api.util.Position;

import java.util.HashMap;
import java.util.Map;

public class DeathMatchGameMap extends GameMap {

    private final Map<DeathMatchTeamType, Position> spawnPoints;

    protected DeathMatchGameMap(String mapName, String worldName, Map<DeathMatchTeamType, Position> spawnPoints) {
        super(mapName, worldName);
        this.spawnPoints = spawnPoints;
    }


    public Location getSpawn(DeathMatchTeamType teamType) {
        return spawnPoints.get(teamType).toLocation(Bukkit.getWorld(getWorldName()));
    }


    public static DeathMatchGameMap.Builder builder() {
        return new DeathMatchGameMap.Builder();
    }


    public static class Builder {

        private Builder() {

        }

        private String worldName;
        private final Map<DeathMatchTeamType, Position> spawnLocation = new HashMap<>();
        private String mapName;

        public DeathMatchGameMap.Builder setWorldName(String worldName) {
            this.worldName = worldName;
            return this;
        }

        public DeathMatchGameMap.Builder setSpawn(DeathMatchTeamType team, Position position) {
            spawnLocation.put(team, position);
            return this;
        }

        public DeathMatchGameMap.Builder setMapName(String mapName) {
            this.mapName = mapName;
            return this;
        }

        public DeathMatchGameMap build() {
            return new DeathMatchGameMap(mapName, worldName, spawnLocation);
        }

    }
}
