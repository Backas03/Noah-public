package org.caramel.backas.noah.game.ocw;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.caramel.backas.noah.game.IGameMap;
import org.caramel.backas.noah.game.ITeamType;
import org.caramel.backas.noah.util.Region;

public class OCWGameMap implements IGameMap {

    private Region region;

    public Region getRegion() {
        return region;
    }

    @Override
    public boolean loadWorld() {
        return false;
    }

    @Override
    public boolean unloadWorld() {
        return false;
    }

    @Override
    public String getWorldName() {
        return null;
    }

    @Override
    public Component getName() {
        return null;
    }

    @Override
    public Location getSpawnPoint(ITeamType type) {
        return null;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void setEnable(boolean enable) {

    }
}
