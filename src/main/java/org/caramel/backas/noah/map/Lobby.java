package org.caramel.backas.noah.map;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.api.game.GameMap;
import org.caramel.backas.noah.api.util.Position;
import org.caramel.backas.noah.api.util.Region;

public class Lobby extends GameMap {

    private final Position spawn;

    public Lobby(Position spawn) {
        super("로비", "lobby");
        this.spawn = spawn;
    }

    public Location getSpawn() {
        return spawn.toLocation(Bukkit.getWorld(getWorldName()));
    }

    public Region getRegion() {
        return new Region(new Position(63, 99, -76), new Position(-84, 0, 69), Bukkit.getWorld(getWorldName()));
    }

    @Override
    public void loadMap() {
        super.loadMap();
        Bukkit.getWorld(getWorldName()).setSpawnLocation(getSpawn());
    }

    @Override
    public void unloadMap() {
        Noah.getInstance().getDebugLogger().warn("로비 맵은 언로드를 할 수 없습니다.");
    }
}
