package org.caramel.backas.noah.game;

import kr.abins.noah.structure.classes.Practice;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.caramel.backas.noah.Lobby;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.blockui.SegmentDisplay;
import org.caramel.backas.noah.game.tdm.TDMGameOption;
import org.caramel.backas.noah.prefix.Prefix;
import org.caramel.backas.noah.ui.InteractableHologram;
import org.caramel.backas.noah.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class GameManager {

    private static GameManager instance;

    public static GameManager getInstance() {
        return instance;
    }

    public static void init(){
        try {
            instance = new GameManager();
        } catch (GameException e) {
            e.printStackTrace();
        }
    }

    @Getter
    private int time;
    private BukkitTask autoStarter;

    @Getter
    private final Map<UUID, Boolean> vote;
    @Getter
    private GameLauncher<?> gameLauncher;
    private IGameMap map;
    private @Getter IGame currentGame;

    private GameManager() throws GameException {
        vote = new HashMap<>();
        selectRandomGame();
        initAutoStartTimer();
    }

    public void start() throws GameException {
        if (isGameStarted()) {
            throw new GameException(Component.text("게임이 이미 시작된 상태입니다", TextColor.color(255, 50, 120)));
        }
        if (gameLauncher == null || map == null) {
            selectRandomGame();
        }
        if (vote.size() < TDMGameOption.MIN_PLAYERS_TO_START) {
            throw new GameException(Component.text("게임 시작 인원이 부족합니다", TextColor.color(255, 50, 120)));
        }
        stopAutoStartTimer();
        Set<User> players = new HashSet<>();
        vote.forEach(((uuid, voted) -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && Practice.isInPractice(player).getFirst()) {
                Practice.exit(player);
            }
            players.add(User.get(uuid));
        }));
        currentGame = gameLauncher.start(map, players);
        // TODO: Event API
        InteractableHologram.Info.updateOnStart();
    }

    public void over() throws GameException {
        if (!isGameStarted()) {
            throw new GameException(Component.text("게임 시작 상태가 아닙니다.", TextColor.color(255, 50, 120)));
        }
        gameLauncher.onOver(currentGame);
        // 게임에서 퇴장하지 않고 투표만 초기화 오프라인이면 불참여 처리
        Iterator<UUID> iterator = vote.keySet().iterator();
        while (iterator.hasNext()) {
            UUID uuid = iterator.next();
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) iterator.remove();
            else {
                ScoreboardManager.activateScoreboard(player); // 메인 스코어보드로 전환
                vote.put(uuid, false);
            }
        }
        currentGame = null;
        selectRandomGame();
        initAutoStartTimer();

        // TODO: Event API
        InteractableHologram.Info.updateOnOver();


    }

    public void selectRandomGame() throws GameException {
        gameLauncher = GameLauncher.random();
        map = gameLauncher.selectMap();
    }

    public IGameMap getMap() {
        return map;
    }

    public boolean hasJoined(Player player) {
        return vote.containsKey(player.getUniqueId());
    }

    public boolean isGameStarted() {
        return currentGame != null;
    }

    public void join(Player player) {
        if (hasJoined(player)) {
            return;
        }
        try {
            vote.put(player.getUniqueId(), false);
            if (currentGame != null) currentGame.onJoin(player);
            else {
                sendInfo();
                player.sendMessage(Prefix.INFO_WITH_SPACE.append(Component.text("게임에 참여하였습니다.")));
                // TODO: Event API
                InteractableHologram.Info.updateOnJoin(player);
            }
        } catch (GameException e) {
            player.sendMessage(e.getComponent()); // 참여 실패 메시지
        }
    }

    public void quit(Player player) {
        if (!vote.containsKey(player.getUniqueId())) {
            return;
        }
        try {
            if (currentGame == null) {
                vote.remove(player.getUniqueId());
                sendInfo();
            } else {
                if (isParticipated(player)) currentGame.onQuit(player);
                vote.remove(player.getUniqueId());
            }
            player.sendMessage(Prefix.INFO_WITH_SPACE.append(Component.text("게임에서 퇴장하셨습니다.")));
            // TODO: Event API
            InteractableHologram.Info.updateOnQuit(player);
        } catch (GameException e) {
            player.sendMessage(e.getComponent()); // 퇴장 실패 메시지
        }
    }

    public boolean isParticipated(UUID uuid) {
        return vote.containsKey(uuid);
    }

    public boolean isParticipated(Player player) {
        return isParticipated(player.getUniqueId());
    }

    public boolean isInGame(UUID uuid) {
        return currentGame != null && isParticipated(uuid);
    }

    public boolean isInGame(Player player) {
        return isInGame(player.getUniqueId());
    }

    public int getVotesToStart() {
        return vote != null ? (int) Math.ceil(vote.size() / 2d) : 0;
    }

    public boolean hasVoted(Player player) {
        return vote.getOrDefault(player.getUniqueId(), false);
    }

    public void vote(Player player) {
        if (vote.size() < TDMGameOption.MIN_PLAYERS_TO_START) {
            player.sendMessage(Prefix.INFO_WITH_SPACE.append(Component.text("인원이 부족하여 게임 시작 투표를 하실 수 없습니다.")));
            return;
        }
        Boolean b = vote.put(player.getUniqueId(), true);
        if (b != null && !b) { // b == null 일시 참여중이 아님
            sendInfo();
            player.sendMessage(Prefix.INFO_WITH_SPACE.append(Component.text("게임 바로시작 투표를 하였습니다.")));
            // TODO: Event API
            InteractableHologram.Info.updateOnVote(player);

            if (getVotes() >= getVotesToStart() && !isGameStarted()) {
                try {
                    start();
                } catch (GameException e) {
                    Bukkit.broadcast(e.getComponent());
                }
            }
        }

    }

    public int getVotes() {
        int values = 0;
        if (vote != null) {
            for (boolean b : vote.values()) {
                if (b) values++;
            }
        }
        return values;
    }

    private void broadcastActionBar(Component component) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (player.getWorld().getName().equals(Lobby.getWorld().getName())) {
                player.sendActionBar(component);
            }
        });
    }

    public int getJoined() {
        return vote.size();
    }

    private void sendInfo() {
        broadcastActionBar(
                Component.text().append(
                        Component.text("게임 시작까지 ", NamedTextColor.WHITE),
                        Component.text(String.format("%d초", time), TextColor.color(200, 120, 150)),
                        Component.text(" | ", NamedTextColor.GRAY),
                        Component.text("참여 인원 ",  NamedTextColor.WHITE),
                        Component.text(String.format("%d명", vote.size()), NamedTextColor.YELLOW),
                        Component.text(" | ", NamedTextColor.GRAY),
                        Component.text("게임 시작 투표 ",  NamedTextColor.WHITE),
                        Component.text(String.format("(%d/%d)", getVotes(), getVotesToStart()), NamedTextColor.GOLD)
                ).build()
        );
    }

    private void initAutoStartTimer() {
        time = 60;
        autoStarter = Bukkit.getScheduler().runTaskTimer(Noah.getInstance(), () -> {
            if (--time == 0) {
                stopAutoStartTimer();
                try {
                    start();
                } catch (GameException e) {
                    broadcastActionBar(e.getComponent());
                    initAutoStartTimer();
                }
            }
            SegmentDisplay.setTime(time);
        }, 20L, 20L);
    }

    private void stopAutoStartTimer() {
        if (autoStarter != null) autoStarter.cancel();
        autoStarter = null;
    }

}
