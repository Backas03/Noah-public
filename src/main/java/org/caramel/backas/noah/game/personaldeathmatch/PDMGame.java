package org.caramel.backas.noah.game.personaldeathmatch;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.caramel.backas.noah.api.game.matching.MatchingPool;
import org.caramel.backas.noah.api.util.Position;
import org.caramel.backas.noah.game.deathmatch.DeathMatchConfig;
import org.caramel.backas.noah.game.deathmatch.DeathMatchGame;
import org.caramel.backas.noah.game.deathmatch.DeathMatchGameMap;
import org.caramel.backas.noah.game.deathmatch.DeathMatchTeamType;
import org.caramel.backas.noah.game.teamdeathmatch.TDMChannel;
import org.caramel.backas.noah.resource.DeathMatchResource;
import org.caramel.backas.noah.util.ItemStackUtil;

import java.util.Arrays;

public class PDMGame extends DeathMatchGame {

    private final PDMConfig config;

    public PDMGame() {
        config = new PDMConfig();
        config.load();
        /*
        getChannelPool().addGameChannel(new PDMChannel(this, DeathMatchGameMap.builder()
                .setMapName("아케인")
                .setWorldName("pdm_arcane")
                .setSpawn(DeathMatchTeamType.RED, new Position(-53.5, 10.5, -5.5, -90, 0))
                .setSpawn(DeathMatchTeamType.BLACK, new Position(54.5, 10.5, 6.5, 90, 0))
                .build()));
        getChannelPool().addGameChannel(new PDMChannel(this, DeathMatchGameMap.builder()
                .setMapName("프렌치 블랙")
                .setWorldName("pdm_french_black")
                .setSpawn(DeathMatchTeamType.RED, new Position(-42.5, 20.5, 8.5, -90, 0))
                .setSpawn(DeathMatchTeamType.BLACK, new Position(43.5, 20.5, -7.5, 90, 0))
                .build()));
        getChannelPool().addGameChannel(new PDMChannel(this, DeathMatchGameMap.builder()
                .setMapName("체르노빌")
                .setWorldName("pdm_chernobyl")
                .setSpawn(DeathMatchTeamType.RED, new Position(3.5, 14.5, 62.5, 90, 0))
                .setSpawn(DeathMatchTeamType.BLACK, new Position(-2.5, 14.5, -61.5, -90, 0))
                .build()));
        getChannelPool().addGameChannel(new PDMChannel(this, DeathMatchGameMap.builder()
                .setMapName("사일로22")
                .setWorldName("pdm_silo22")
                .setSpawn(DeathMatchTeamType.RED, new Position(-16.5, 86.5, 5.5, -180, 0))
                .setSpawn(DeathMatchTeamType.BLACK, new Position(19.5, 86.5, -3.5, 0, 0))
                .build()));
        getChannelPool().addGameChannel(new PDMChannel(this, DeathMatchGameMap.builder()
                .setMapName("포레스트")
                .setWorldName("pdm_forest")
                .setSpawn(DeathMatchTeamType.RED, new Position(70.5, 75.5, 19.5, -180, 0))
                .setSpawn(DeathMatchTeamType.BLACK, new Position(-65.5, 75.5, -12.5, 0, 0))
                .build()));
        getChannelPool().addGameChannel(new PDMChannel(this, DeathMatchGameMap.builder()
                .setMapName("버려진 공업단지")
                .setWorldName("pdm_sewer")
                .setSpawn(DeathMatchTeamType.RED, new Position(0.5, 123.5, 64.5, 0, 0))
                .setSpawn(DeathMatchTeamType.BLACK, new Position(1.5, 123.5, -63.5, -180, 0))
                .build()));
        getChannelPool().addGameChannel(new PDMChannel(this, DeathMatchGameMap.builder()
                .setMapName("베살루")
                .setWorldName("pdm_rpg")
                .setSpawn(DeathMatchTeamType.RED, new Position(-59, 120.5, -4, -90, 0))
                .setSpawn(DeathMatchTeamType.BLACK, new Position(41, 122.5, -8, 90, 0))
                .build()));
        getChannelPool().addGameChannel(new PDMChannel(this, DeathMatchGameMap.builder()
                .setMapName("리마")
                .setWorldName("pdm_m2")
                .setSpawn(DeathMatchTeamType.RED, new Position(-49.5, 109.5, 124.5, -180, 0))
                .setSpawn(DeathMatchTeamType.BLACK, new Position(-49.5, 109.5, 20.5, 0, 0))
                .build()));
        getChannelPool().addGameChannel(new PDMChannel(this, DeathMatchGameMap.builder()
                .setMapName("노아상무")
                .setWorldName("pdm_company")
                .setSpawn(DeathMatchTeamType.RED, new Position(45.5, 120.5, -25.5, 0, 0))
                .setSpawn(DeathMatchTeamType.BLACK, new Position(-49.5, 120.5, 24.5, 180, 0))
                .build()));

         */

        getChannelPool().addGameChannel(new PDMChannel(this, DeathMatchGameMap.builder()
                .setMapName("해양 연구소")
                .setWorldName("pdm_sea_lab")
                .setSpawn(DeathMatchTeamType.RED, new Position(-28.5, 20.5, 5.5, 0, 0))
                .setSpawn(DeathMatchTeamType.BLACK, new Position(29.5, 20.5, -3.5, 180, 0))
                .build()));
        getChannelPool().addGameChannel(new PDMChannel(this, DeathMatchGameMap.builder()
                .setMapName("산토리니")
                .setWorldName("pdm_white_village")
                .setSpawn(DeathMatchTeamType.RED, new Position(4.5, 27.5, -113.5,  0, 0))
                .setSpawn(DeathMatchTeamType.BLACK, new Position(-3.5, 27.5, 114.5, -180, 0))
                .build()));
        getChannelPool().addGameChannel(new PDMChannel(this, DeathMatchGameMap.builder()
                .setMapName("베네스")
                .setWorldName("pdm_venice")
                .setSpawn(DeathMatchTeamType.RED, new Position(25.5, 124.5, -51.5, 90, 0))
                .setSpawn(DeathMatchTeamType.BLACK, new Position(-24.5, 124.5, 52.5, -90, 0))
                .build()));
        getChannelPool().addGameChannel(new PDMChannel(this, DeathMatchGameMap.builder()
                .setMapName("군부대")
                .setWorldName("pdm_military")
                .setSpawn(DeathMatchTeamType.RED, new Position(-7.5, 130.5, 40.5, -90, 0))
                .setSpawn(DeathMatchTeamType.BLACK, new Position(8.5, 130.5, -40.5, 90, 0))
                .build()));
        getChannelPool().addGameChannel(new PDMChannel(this, DeathMatchGameMap.builder()
                .setMapName("리장마을")
                .setWorldName("pdm_rizang")
                .setSpawn(DeathMatchTeamType.RED, new Position(-58.5, 34.5, -8.5, -90, 0))
                .setSpawn(DeathMatchTeamType.BLACK, new Position(56.5, 34.5, 16.5, 90, 0))
                .build()));
    }

    @Override
    public String getName() {
        return "1 VS 1 데스매치";
    }

    @Override
    public ItemStack getIcon() {
        return DeathMatchResource.getPDMIcon(this);
    }

    @Override
    protected MatchingPool newMatchingPool() {
        return new PDMMatchingPool(this, 600, 3);
    }

    @Override
    public DeathMatchConfig getConfig() {
        return config;
    }
}
