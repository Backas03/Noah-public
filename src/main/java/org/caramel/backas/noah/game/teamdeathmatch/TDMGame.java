package org.caramel.backas.noah.game.teamdeathmatch;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.caramel.backas.noah.api.game.matching.MatchingPool;
import org.caramel.backas.noah.api.util.Position;
import org.caramel.backas.noah.game.deathmatch.DeathMatchConfig;
import org.caramel.backas.noah.game.deathmatch.DeathMatchGame;
import org.caramel.backas.noah.game.deathmatch.DeathMatchGameMap;
import org.caramel.backas.noah.game.deathmatch.DeathMatchTeamType;
import org.caramel.backas.noah.resource.DeathMatchResource;
import org.caramel.backas.noah.util.ItemStackUtil;

import java.util.*;

public class TDMGame extends DeathMatchGame {

    private final TDMConfig config;

    public TDMGame() {
        config = new TDMConfig();
        config.load();
        getChannelPool().addGameChannel(new TDMChannel(this, DeathMatchGameMap.builder()
                .setMapName("산토리니")
                .setWorldName("tdm_white_village")
                .setSpawn(DeathMatchTeamType.RED, new Position(4.5, 27.5, -113.5,  0, 0))
                .setSpawn(DeathMatchTeamType.BLACK, new Position(-3.5, 27.5, 114.5, -180, 0))
                .build()));
        getChannelPool().addGameChannel(new TDMChannel(this, DeathMatchGameMap.builder()
                .setMapName("아케인")
                .setWorldName("tdm_arcane")
                .setSpawn(DeathMatchTeamType.RED, new Position(-53.5, 10.5, -5.5, -90, 0))
                .setSpawn(DeathMatchTeamType.BLACK, new Position(54.5, 10.5, 6.5, 90, 0))
                .build()));
        getChannelPool().addGameChannel(new TDMChannel(this, DeathMatchGameMap.builder()
                .setMapName("프렌치 블랙")
                .setWorldName("tdm_french_black")
                .setSpawn(DeathMatchTeamType.RED, new Position(-42.5, 20.5, 8.5, -90, 0))
                .setSpawn(DeathMatchTeamType.BLACK, new Position(43.5, 20.5, -7.5, 90, 0))
                .build()));
        getChannelPool().addGameChannel(new TDMChannel(this, DeathMatchGameMap.builder()
                .setMapName("sci")
                .setWorldName("tdm_sci")
                .setSpawn(DeathMatchTeamType.RED, new Position(55.5, 7.5, 0.5, 90, 0))
                .setSpawn(DeathMatchTeamType.BLACK, new Position(-54, 7.5, 0.5, -90, 0))
                .build()));
        getChannelPool().addGameChannel(new TDMChannel(this, DeathMatchGameMap.builder()
                .setMapName("체르노빌")
                .setWorldName("tdm_chernobyl")
                .setSpawn(DeathMatchTeamType.RED, new Position(3.5, 14.5, 62.5, 90, 0))
                .setSpawn(DeathMatchTeamType.BLACK, new Position(-2.5, 14.5, -61.5, -90, 0))
                .build()));
        getChannelPool().addGameChannel(new TDMChannel(this, DeathMatchGameMap.builder()
                .setMapName("사일로22")
                .setWorldName("tdm_silo22")
                .setSpawn(DeathMatchTeamType.RED, new Position(-16.5, 86.5, 5.5, -180, 0))
                .setSpawn(DeathMatchTeamType.BLACK, new Position(19.5, 86.5, -3.5, 0, 0))
                .build()));
        getChannelPool().addGameChannel(new TDMChannel(this, DeathMatchGameMap.builder()
                .setMapName("해양 연구소")
                .setWorldName("tdm_sea_lab")
                .setSpawn(DeathMatchTeamType.RED, new Position(-28.5, 20.5, 5.5, 0, 0))
                .setSpawn(DeathMatchTeamType.BLACK, new Position(29.5, 20.5, -3.5, 180, 0))
                .build()));
        getChannelPool().addGameChannel(new TDMChannel(this, DeathMatchGameMap.builder()
                .setMapName("포레스트")
                .setWorldName("tdm_forest")
                .setSpawn(DeathMatchTeamType.RED, new Position(70.5, 75.5, 19.5, -180, 0))
                .setSpawn(DeathMatchTeamType.BLACK, new Position(-65.5, 75.5, -12.5, 0, 0))
                .build()));
        getChannelPool().addGameChannel(new TDMChannel(this, DeathMatchGameMap.builder()
                .setMapName("베네스")
                .setWorldName("tdm_venice")
                .setSpawn(DeathMatchTeamType.RED, new Position(25.5, 124.5, -51.5, 90, 0))
                .setSpawn(DeathMatchTeamType.BLACK, new Position(-24.5, 124.5, 52.5, -90, 0))
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
                .setMapName("군부대")
                .setWorldName("tdm_military")
                .setSpawn(DeathMatchTeamType.RED, new Position(-7.5, 130.5, 40.5, -90, 0))
                .setSpawn(DeathMatchTeamType.BLACK, new Position(8.5, 130.5, -40.5, 90, 0))
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
