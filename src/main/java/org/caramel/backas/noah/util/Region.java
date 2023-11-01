package org.caramel.backas.noah.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import java.util.HashSet;
import java.util.Set;

@Getter
@AllArgsConstructor
public class Region {

    private final Position pos1;
    private final Position pos2;
    private final World world;

    public Location getMidPoint(boolean calculateHighestBlock) {
        int x = (pos1.getBlockX() + pos2.getBlockX()) / 2;
        int z = (pos1.getBlockZ() + pos2.getBlockZ()) / 2;
        return calculateHighestBlock ? world.getHighestBlockAt(x, z).getLocation() : new Location(
                world,
                x,
                (pos1.getBlockY() + pos2.getBlockY()) / 2.0,
                z
        );
    }

    public Set<Player> getPlayers() {
        Set<Player> value = new HashSet<>();
        world.getPlayers().forEach(player -> {
            if (isInside(player)) value.add(player);
        });
        return value;
    }

    public boolean isInside(Player player) {
        return isInside(player.getLocation());
    }

    public boolean isInside(Location location) {
        if (location == null) return false;
        if (!location.getWorld().equals(getWorld())) return false;
        int min_x = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int min_y = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int min_z = Math.min(pos1.getBlockZ(), pos2.getBlockZ());

        int max_x = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int max_y = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int max_z = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
        if (min_x > location.getBlockX() || max_x < location.getBlockX()) {
            return false;
        }
        if (min_y > location.getBlockY() || max_y < location.getBlockY()) {
            return false;
        }
        return min_z <= location.getBlockZ() && max_z >= location.getBlockZ();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Region compare) {
            return compare.world.equals(world)
                    && compare.pos1.equals(pos1)
                    && compare.pos2.equals(pos2);
        }
        return false;
    }
}
