package org.caramel.backas.noah.game;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.caramel.backas.noah.game.tdm.TDMTeam;

public interface IGameMap {

    boolean loadWorld();

    boolean unloadWorld();

    String getWorldName();

    Component getName();

    Location getSpawnPoint(ITeamType type);

    boolean isEnabled();

    void setEnable(boolean enable);

    default World getWorld() {
        return Bukkit.getWorld(getWorldName());
    }

    default boolean isLoaded() {
        return Bukkit.getWorld(getWorldName()) != null;
    }
}
