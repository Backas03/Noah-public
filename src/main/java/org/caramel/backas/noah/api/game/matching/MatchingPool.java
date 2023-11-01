package org.caramel.backas.noah.api.game.matching;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.api.event.game.GameDequeueEvent;
import org.caramel.backas.noah.api.event.game.GameEnqueueEvent;
import org.caramel.backas.noah.api.game.*;
import org.caramel.backas.noah.api.party.Party;
import org.caramel.backas.noah.api.party.PartyManager;
import org.caramel.backas.noah.api.user.User;
import org.caramel.backas.noah.api.user.model.CurrentGameData;

import java.time.Duration;
import java.util.*;

@Getter
public abstract class MatchingPool {

    private static Map<Class<? extends AbstractGame>, LinkedHashMap<Party, MatchingParty>> queue;
    private static final MatchingConfig config;

    static {
        config = new MatchingConfig();
        config.load();
        queue = new HashMap<>();
    }

    public static void enqueue(Class<? extends AbstractGame> gameType, User user) {
        if (!user.hasParty()) {
            PartyManager.createNewParty(user);
        }
        enqueue(gameType, user.getParty());
    }

    public static void enqueue(Class<? extends AbstractGame> gameType, Party party) {
        AbstractGame game = GameManager.getInstance().getGame(gameType);
        TextComponent title = Component.text()
                .content("대기열 입장 실패")
                .color(NamedTextColor.RED)
                .build();
        if (game == null) {
            party.sendTitle(Title.title(
                title,
                Component.text(config.getGameNotFound().get(), NamedTextColor.GRAY),
                Title.Times.times(Duration.ofMillis(0), Duration.ofMillis(1500), Duration.ofMillis(1000))
            ));
            return;
        }
        if (isMatching(party)) {
            party.sendTitle(Title.title(
                title,
                Component.text(config.getPartyIsMatching().get(), NamedTextColor.GRAY),
                Title.Times.times(Duration.ofMillis(0), Duration.ofMillis(1500), Duration.ofMillis(1000))
            ));
            return;
        }
        if (queue == null) queue = new LinkedHashMap<>();
        MatchingPool pool = game.getMatchingPool();
        if (pool.canEnqueue(party)) {
            MatchingParty matchingParty = new MatchingParty(party, game);
            queue.computeIfAbsent(gameType, k -> new LinkedHashMap<>()).put(party, matchingParty);
            Bukkit.getPluginManager().callEvent(new GameEnqueueEvent(party, game));
        }
    }

    public static void dequeue(Party party) {
        Class<? extends AbstractGame> gameType = getEnqueuedGame(party);
        if (gameType == null) { // is not matching
            party.sendTitle(Title.title(
                Component.text("매칭 취소 실패", NamedTextColor.RED),
                Component.text(config.getNotInQueue().get(), NamedTextColor.GRAY),
                Title.Times.times(Duration.ofMillis(0), Duration.ofMillis(1500), Duration.ofMillis(1000))
            ));
            return;
        }
        queue.get(gameType).get(party).cancelTimer();
        MatchingPool.queue.get(gameType).remove(party); // dequeue
        AbstractGame game = GameManager.getInstance().getGame(gameType);
        if (game != null) {
            party.sendTitle(Title.title(
                Component.text("매칭 취소", NamedTextColor.RED),
                Component.text(game.getName(), NamedTextColor.YELLOW),
                Title.Times.times(Duration.ofMillis(0), Duration.ofMillis(1500), Duration.ofMillis(1000))
            ));
            Bukkit.getPluginManager().callEvent(new GameDequeueEvent(party, game));
        }
    }

    public static Class<? extends AbstractGame> getEnqueuedGame(Party party) {
        if (queue != null) {
            for (Map.Entry<Class<? extends AbstractGame>, LinkedHashMap<Party, MatchingParty>> entry : queue.entrySet()) {
                if (entry.getValue() != null && entry.getValue().containsKey(party)) return entry.getKey();
            }
        }
        return null;
    }

    public static boolean isMatching(Party party) {
        if (queue != null) {
            for (LinkedHashMap<Party, MatchingParty> values : queue.values()) {
                if (values.containsKey(party)) return true;
            }
        }
        return false;
    }

    public abstract boolean canEnqueue(Party party);
    protected abstract Map<Party, IGameTeam> distribute(List<Party> parties);
    private final AbstractGame game;
    private int maxMMRDiff;
    private int mmrPerSecond;
    private BukkitTask matchingTask;

    public void startMatchingTask() {
        stopMatchingTask();
        matchingTask = Bukkit.getScheduler().runTaskTimer(Noah.getInstance(), () -> {
            ChannelPool pool = game.getChannelPool();
            if (!pool.isEmpty() && MatchingPool.queue != null) {
                LinkedHashMap<Party, MatchingParty> queue = MatchingPool.queue.get(game.getClass());
                if (queue != null) {
                    Map<Party, Set<Party>> result = new HashMap<>();
                    LinkedList<Party> enqueued = new LinkedList<>(queue.keySet());
                    for (int i=0; i<enqueued.size(); i++) {
                        Party party = enqueued.get(i);
                        MatchingParty matchingParty = queue.get(party);
                        if (matchingParty != null) {
                            int avgMMR = matchingParty.getAvgMMR();
                            int diffMMR = matchingParty.getTime() * mmrPerSecond;
                            for (int j=i+1; j<enqueued.size(); j++) {
                                Party compareParty = enqueued.get(j);
                                MatchingParty compare = queue.get(compareParty);
                                if (compare != null) {
                                    int compareAvgMMR = compare.getAvgMMR();
                                    int compareDiffMMR = compare.getTime() * mmrPerSecond;
                                    int d = Math.abs(avgMMR - compareAvgMMR);
                                    if (d == 0 || d <= diffMMR + compareDiffMMR) {
                                        result.computeIfAbsent(party, k -> new HashSet<>()).add(compareParty);
                                    }
                                }
                            }
                        }
                    }
                    GameChannel<?> channel = pool.emptyChannelPool().getRandomly();
                    if (channel != null) {
                        for (Map.Entry<Party, Set<Party>> entry : result.entrySet()) {
                            List<Party> parties = new ArrayList<>(entry.getValue());
                            parties.add(entry.getKey());
                            if (parties.size() > 1) {
                                Map<Party, IGameTeam> matched = distribute(parties);
                                if (matched != null && channel.isEmpty() && channel.canStart()) {
                                    for (Party party : matched.keySet()) {
                                        MatchingParty matchingParty = queue.get(party);
                                        if (matchingParty != null) {
                                            matchingParty.cancelTimer();
                                            MatchingPool.queue.get(game.getClass()).remove(party); // dequeue
                                            matchingParty.getParty().getAllMembers().forEach(user -> user.setGameData(new CurrentGameData(game, channel)));
                                        }
                                    }
                                    channel.start(matched);
                                }
                            }
                        }
                    }
                }
            }
        }, 0L, 20L);
    }

    public void stopMatchingTask() {
        if (matchingTask != null) matchingTask.cancel();
        matchingTask = null;
    }

    public MatchingPool(AbstractGame game, int maxMMRDiff, int mmrPerSecond) {
        this.game = game;
        this.maxMMRDiff = maxMMRDiff;
        this.mmrPerSecond = mmrPerSecond;
        startMatchingTask();
    }

    public void setMaxMMRDiff(int value) {
        maxMMRDiff = value;
    }

    public void setMMRPerSecond(int value) {
        mmrPerSecond = value;
    }

    public int getMMRPerSecond() {
        return mmrPerSecond;
    }

}
