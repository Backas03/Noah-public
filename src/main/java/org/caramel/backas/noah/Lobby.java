package org.caramel.backas.noah;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.caramel.backas.noah.util.Position;
import org.caramel.backas.noah.util.Region;

public class Lobby {

    public static final String WORLD_NAME = "lobby";

    public static Location getSpawnPoint() {
        return new Location(getWorld(), -30.5, 23, 0.5, 90,  -6);
    }

    public static World getWorld() {
        return Bukkit.getWorld(WORLD_NAME);
    }

    public static Region getRegion() {
        Position p1 = new Position(63, 99, -76);
        Position p2 = new Position(-84, 0, 69);
        return new Region(p1, p2, getWorld());
    }

    public static void teleportToSpawn(Player player) {
        player.teleport(getSpawnPoint());
    }
}
