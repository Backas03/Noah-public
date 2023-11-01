package org.caramel.backas.noah.util;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;

@Getter
@Setter
public class Position {

    private double x;
    private double y;
    private double z;

    private float yaw;
    private float pitch;

    public Position(Location location) {
        this(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public Position(double x, double y, double z) {
        this(x, y, z, 0, 0);
    }

    public Position(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public int getBlockX() {
        return (int) Math.round(x);
    }

    public int getBlockY() {
        return (int) Math.round(y);
    }

    public int getBlockZ() {
        return (int) Math.round(z);
    }

    public Location toLocation(World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Position compare) {
            return compare.x == x
                    && compare.y == y
                    && compare.z == z;
        }
        return false;
    }
}
