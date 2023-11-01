package org.caramel.backas.noah;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.caramel.backas.noah.api.afk.AFKScheduler;
import org.caramel.backas.noah.api.game.AbstractGame;
import org.caramel.backas.noah.api.game.GameManager;
import org.caramel.backas.noah.api.game.matching.MatchingPool;
import org.caramel.backas.noah.api.game.rating.Rating;
import org.caramel.backas.noah.api.party.Party;
import org.caramel.backas.noah.api.tier.Ranker;
import org.caramel.backas.noah.api.tier.RankingConfig;
import org.caramel.backas.noah.api.user.User;
import org.caramel.backas.noah.api.user.model.CashPoint;
import org.caramel.backas.noah.api.user.model.NoahPoint;
import org.caramel.backas.noah.api.user.model.Point;
import org.caramel.backas.noah.api.util.Position;
import org.caramel.backas.noah.cmd.*;
import org.caramel.backas.noah.game.deathmatch.DeathMatchGame;
import org.caramel.backas.noah.game.deathmatch.DeathMatchListener;
import org.caramel.backas.noah.game.personaldeathmatch.PDMGame;
import org.caramel.backas.noah.game.teamdeathmatch.TDMGame;
import org.caramel.backas.noah.listener.GameListener;
import org.caramel.backas.noah.listener.PartyListener;
import org.caramel.backas.noah.tablist.TabListListener;
import org.caramel.backas.noah.listener.UserListener;
import org.caramel.backas.noah.map.Lobby;
import org.caramel.backas.noah.util.DebugLogger;

import java.io.IOException;
import java.util.ArrayList;

public final class Noah extends JavaPlugin {

    @Getter
    private static Noah instance;
    @Getter
    private DebugLogger debugLogger;
    @Getter
    private static Lobby lobby;
    @Getter
    private RankingConfig rankingData;


    @Override
    public void onEnable() {
        instance = this;
        AFKScheduler.getInstance().runScheduler();
        debugLogger = new DebugLogger(this);
        lobby = new Lobby(new Position(0, 7, 0));
        lobby.loadMap();
        registerAllModels();
        rankingData = new RankingConfig();
        rankingData.load();
        registerAllCommands();
        loadAllOnlineUsers();
        Bukkit.getPluginManager().registerEvents(new PartyListener(), this);
        Bukkit.getPluginManager().registerEvents(new UserListener(), this);
        Bukkit.getPluginManager().registerEvents(new GameListener(), this);
        Bukkit.getPluginManager().registerEvents(new TabListListener(), this);
        Bukkit.getPluginManager().registerEvents(new DeathMatchListener(), this);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, User::doDataManage, 0L, 20 * 60 * 5);
        initGames();
    }

    @Override
    public void onDisable() {
        try {
            getRankingData().save();
        } catch (IOException e) {
            e.printStackTrace();
        }
        unloadAllMaps();
        saveAllUserData();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(getLobby().getSpawn());
        }
        for (World world : Bukkit.getWorlds()) {
            if (!world.getName().equals("lobby")) {
                for (Player player : world.getPlayers()) {
                    player.teleport(getLobby().getSpawn());
                }
                Bukkit.unloadWorld(world, false);
            }
        }
    }

    private void saveAllUserData() {
        User.getAll().forEach(user -> {
            try {
                user.saveData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void unloadAllMaps() {
        for (World world : Bukkit.getWorlds()) {
            if (world.getName().equals(lobby.getWorldName())) continue;
            Bukkit.getServer().unloadWorld(world, true);
        }
    }

    private void initGames() {
        GameManager.getInstance().registerNewGame(new TDMGame());
        GameManager.getInstance().registerNewGame(new PDMGame());
    }

    public void reloadGames() {
        for (User user : User.getAll()) {
            if (user.hasParty()) {
                Party party = user.getParty();
                if (MatchingPool.isMatching(party)) {
                    MatchingPool.dequeue(party);
                }
            }
        }
        for (AbstractGame game : new ArrayList<>(GameManager.getInstance().getRegisteredGames())) {
            GameManager.getInstance().unregisterGame(game);
        }
        initGames();
    }

    private void loadAllOnlineUsers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                User.get(player).loadData();
            } catch (IOException e) {
                getDebugLogger().error("유저 데이터 로딩 중 오류 발생 : " + e + " - " + player.getName());
            }
        }
    }

    private void saveAllConfigs() {
        try {
            for (AbstractGame game : GameManager.getInstance().getRegisteredGames()) {
                if (game instanceof DeathMatchGame) {
                    ((DeathMatchGame) game).getConfig().save();
                }
            }
            getRankingData().save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerAllModels() {
        ConfigurationSerialization.registerClass(Rating.class);
        ConfigurationSerialization.registerClass(Ranker.class);
        ConfigurationSerialization.registerClass(NoahPoint.class);
        ConfigurationSerialization.registerClass(CashPoint.class);
        ConfigurationSerialization.registerClass(Point.class);
    }

    private void registerAllCommands() {
        getServer().registerCommand(this, "게임관리", new CmdAdminGame());
        getServer().registerCommand(this, "mmr", new CmdUserRating());
        getServer().registerCommand(this, "ranking", new CmdUserRanking());
        getServer().registerCommand(this, "config", new CmdAdminConfig());
        getServer().registerCommand(this, "noahpoint", new CmdUserNoahPoint());
        getServer().registerCommand(this, "cash", new CmdUserCashPoint());
    }


}
