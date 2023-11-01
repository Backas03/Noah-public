package org.caramel.backas.noah.api.afk;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.UUID;

@Setter
@Getter
public class AFKCache {

    private Location lastLocation;
    private int second;

    public AFKCache() {
        this.lastLocation = null;
        this.second = 0;
    }

    public boolean isAFK() {
        return second >= AFKScheduler.getInstance().getTimeToAFK();
    }

}
