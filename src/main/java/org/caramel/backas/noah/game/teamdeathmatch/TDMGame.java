package org.caramel.backas.noah.game.teamdeathmatch;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.caramel.backas.noah.api.game.matching.MatchingPool;
import org.caramel.backas.noah.api.util.Position;
import org.caramel.backas.noah.game.deathmatch.DeathMatchConfig;
import org.caramel.backas.noah.game.deathmatch.DeathMatchGame;
import org.caramel.backas.noah.game.deathmatch.DeathMatchGameMap;
import org.caramel.backas.noah.game.deathmatch.DeathMatchTeamType;
import org.caramel.backas.noah.game.personaldeathmatch.PDMChannel;
import org.caramel.backas.noah.resource.DeathMatchResource;
import org.caramel.backas.noah.util.ItemStackUtil;

import java.util.*;

public class TDMGame extends DeathMatchGame {

    private final TDMConfig config;

    public TDMGame() {
        config = new TDMConfig();
        config.load();
        getChannelPool().addGameChannel(new TDMChannel(this, DeathMatchGameMap.builder()
                .setMapName("아케인")
                .setWorldName("tdm_arcane")
                .setSpawn(DeathMatchTeamType.RED, new Position(-53.5, 10.5, -5.5, -90, 0))
                .setSpawn(DeathMatchTeamType.BLACK, new Position(54.5, 10.5, 6.5, 90, 0))
                .build()));
        getChannelPool().addGameChannel(new TDMChannel(this, DeathMatchGameMap.builder()
                .setMapName("체르노빌")
                .setWorldName("tdm_chernobyl")
                .setSpawn(DeathMatchTeamType.RED, new Position(3.5, 14.5, 62.5, 90, 0))
                .setSpawn(DeathMatchTeamType.BLACK, new Position(-2.5, 14.5, -61.5, -90, 0))
                .build()));
        getChannelPool().addGameChannel(new TDMChannel(this, DeathMatchGameMap.builder()
                .setMapName("포레스트")
                .setWorldName("tdm_forest")
                .setSpawn(DeathMatchTeamType.RED, new Position(70.5, 75.5, 19.5, -180, 0))
                .setSpawn(DeathMatchTeamType.BLACK, new Position(-65.5, 75.5, -12.5, 0, 0))
                .build()));
        getChannelPool().addGameChannel(new TDMChannel(this, DeathMatchGameMap.builder()
                .setMapName("버려진 공업단지")
                .setWorldName("tdm_sewer")
                .setSpawn(DeathMatchTeamType.RED, new Position(0, 123.5, 64, 0, 0))
                .setSpawn(DeathMatchTeamType.BLACK, new Position(1, 123.5, -63, -180, 0))
                .build()));
        getChannelPool().addGameChannel(new TDMChannel(this, DeathMatchGameMap.builder()
                .setMapName("베살루")
                .setWorldName("tdm_rpg")
                .setSpawn(DeathMatchTeamType.RED, new Position(-59, 120.5, -4, -90, 0))
                .setSpawn(DeathMatchTeamType.BLACK, new Position(41, 122.5, -8, 90, 0))
                .build()));
        getChannelPool().addGameChannel(new TDMChannel(this, DeathMatchGameMap.builder()
                .setMapName("리마")
                .setWorldName("tdm_m2")
                .setSpawn(DeathMatchTeamType.RED, new Position(-49.5, 109.5, 124.5, -180, 0))
                .setSpawn(DeathMatchTeamType.BLACK, new Position(-49.5, 109.5, 20.5, 0, 0))
                .build()));
        getChannelPool().addGameChannel(new TDMChannel(this, DeathMatchGameMap.builder()
                .setMapName("노아상무")
                .setWorldName("tdm_company")
                .setSpawn(DeathMatchTeamType.RED, new Position(45.5, 120.5, -25.5, 0, 0))
                .setSpawn(DeathMatchTeamType.BLACK, new Position(-49.5, 120.5, 24.5, 180, 0))
                .build()));
        getChannelPool().addGameChannel(new TDMChannel(this, DeathMatchGameMap.builder()
                .setMapName("리장마을")
                .setWorldName("tdm_rizang")
                .setSpawn(DeathMatchTeamType.RED, new Position(-58.5, 34.5, -8.5, -90, 0))
                .setSpawn(DeathMatchTeamType.BLACK, new Position(56.5, 34.5, 16.5, 90, 0))
                .build()));
    }


    @Override
    public String getName() {
        return "팀 데스매치";
    }

    @Override
    public ItemStack getIcon() {
        return DeathMatchResource.getTDMIcon(this);
    }

    @Override
    protected MatchingPool newMatchingPool() {
        return new TDMMatchingPool(this, 300, 10);
    }

    @Override
    public DeathMatchConfig getConfig() {
        return config;
    }
}
