package org.caramel.backas.noah.api.user;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.api.afk.AFKCache;
import org.caramel.backas.noah.api.event.user.UserDataLoadDoneEvent;
import org.caramel.backas.noah.api.event.user.UserDataUnloadEvent;
import org.caramel.backas.noah.api.game.AbstractGame;
import org.caramel.backas.noah.api.game.matching.MatchingPool;
import org.caramel.backas.noah.api.game.rating.Rating;
import org.caramel.backas.noah.api.party.Party;
import org.caramel.backas.noah.api.user.model.CashPoint;
import org.caramel.backas.noah.api.user.model.CurrentGameData;
import org.caramel.backas.noah.api.user.model.NoahPoint;
import org.caramel.backas.noah.util.ColorString;
import org.caramel.backas.noah.util.FileUtil;
import org.caramel.backas.noah.util.OfflinePlayerUtil;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class User {

    public static File getDataFolder() {
        return new File(Noah.getInstance().getDataFolder(), "users");
    }

    public static void doDataManage() {
        Noah.getInstance().getDebugLogger().info(instance.values().size() + "명의 로딩된 유저 데이터 저장 시작...");
        Set<UUID> unloaded = new HashSet<>();
        int saved = 0;
        int error = 0;
        for (User user : instance.values()) {
            try {
                user.saveData();
                saved++;
            } catch (IOException e) {
                Noah.getInstance().getDebugLogger().error("유저 데이터 저장 중 오류 발생 : " + e + " - " + user);
                error++;
            } finally {
                if (user.needUnload) {
                    unloaded.add(user.uniqueId);
                }
            }
        }
        unloaded.forEach(uuid -> {
            Bukkit.getScheduler().runTask(Noah.getInstance(), () -> Bukkit.getPluginManager().callEvent(new UserDataUnloadEvent(instance.get(uuid))));
            instance.remove(uuid);
        });
        Noah.getInstance().getDebugLogger().info("유저 데이터 저장 완료! (성공: " + saved + ", 실패: " + error + ", 언로드: " + unloaded.size() + ")");
    }

    private static final Map<UUID, User> instance = new HashMap<>();

    public static User get(UUID uuid) {
        return instance.computeIfAbsent(uuid, k -> new User(uuid));
    }

    public static User get(Player player) {
        return get(player.getUniqueId());
    }

    public static Collection<User> getAll() {
        return instance.values();
    }

    @Getter
    private final UUID uniqueId;
    @Getter
    private boolean needUnload = false;
    @Getter
    private YamlConfiguration data;
    @Setter @Getter
    private CurrentGameData gameData;
    @Getter @Setter
    private Party party;
    @Getter
    private final Set<Party> invitedParties = new HashSet<>();
    @Getter
    private final AFKCache afkCache;

    public boolean isInQueue() {
        return party != null && MatchingPool.isMatching(party);
    }

    public void addInvitedParty(Party party) {
        invitedParties.add(party);
    }

    public boolean hasParty() {
        return party != null;
    }

    public boolean hasInvitedParty(Party party) {
        return invitedParties.contains(party);
    }

    private User(UUID uuid) {
        this.uniqueId = uuid;
        afkCache = new AFKCache();
    }

    public boolean isOnline() {
        return toPlayer() != null;
    }

    @Nullable
    public Player toPlayer() {
        return Bukkit.getPlayer(uniqueId);
    }

    public OfflinePlayer toOfflinePlayer() {
        return OfflinePlayerUtil.getFromOffline(uniqueId);
    }

    public boolean isLoad() {
        return data != null;
    }

    public void setNeedUnload(boolean needUnload) {
        if (gameData != null) {
            this.needUnload = false;
            return;
        }
        this.needUnload = needUnload;
    }

    public Rating getRating(Class<? extends AbstractGame> gameType) {
        Rating rating = data.getSerializable("Rating." + gameType.getSimpleName(), Rating.class);
        if (rating == null) {
            rating = new Rating();
            data.set("Rating." + gameType.getSimpleName(), rating);
            Noah.getInstance().getRankingData().updateRanking(uniqueId, getName(), rating, gameType);
        }
        return rating;
    }

    public void setRating(Class<? extends AbstractGame> gameType, Rating rating) {
        data.set("Rating." + gameType.getSimpleName(), rating);
        Noah.getInstance().getRankingData().updateRanking(uniqueId, getName(), rating, gameType);
    }

    public boolean isInGame() {
        return party != null && !MatchingPool.isMatching(party) && gameData != null;
    }

    public Location getLocation() {
        Player player = toPlayer();
        return player == null ? null : player.getLocation();
    }

    public void sendMessage(String msg) {
        Player player = toPlayer();
        if (player != null) {
            player.sendMessage(ColorString.parse(msg));
        }
    }

    public void sendMessage(TextComponent component) {
        Player player = toPlayer();
        if (player != null) {
            player.sendMessage(component);
        }
    }

    public void sendTitle(Title title) {
        Player player = toPlayer();
        if (player != null) {
            player.showTitle(title);
        }
    }

    public NoahPoint getNoahPoint() {
        NoahPoint noahPoint = data.getSerializable(NoahPoint.KEY, NoahPoint.class);
        if (noahPoint == null) {
            noahPoint = new NoahPoint(0);
            data.set(NoahPoint.KEY, noahPoint);
        }
        return noahPoint;
    }

    public CashPoint getCashPoint() {
        CashPoint cashPoint = data.getSerializable(CashPoint.KEY, CashPoint.class);
        if (cashPoint == null) {
            cashPoint = new CashPoint(0);
            data.set(CashPoint.KEY, cashPoint);
        }
        return cashPoint;
    }

    public boolean hasLoaded() {
        return data != null;
    }

    public void loadData() throws IOException {
        checkAndCreateDataFile();
        if (needUnload) return;
        data = YamlConfiguration.loadConfiguration(getDataFile());
        Bukkit.getScheduler().runTask(Noah.getInstance(), () -> Bukkit.getPluginManager().callEvent(new UserDataLoadDoneEvent(this)));
    }

    public void saveData() throws IOException {
        if (!hasLoaded()) return;
        checkAndCreateDataFile();
        data.save(getDataFile());
    }

    public void checkAndCreateDataFile() throws IOException {
        FileUtil.checkAndCreateFile(getDataFile());
    }

    public CompletableFuture<Boolean> teleportAsync(Location location) {
        Player player = toPlayer();
        if (player == null) return null;
        return player.teleportAsync(location);
    }

    public File getDataFile() {
        return new File(getDataFolder(), uniqueId + ".yml");
    }

    public void sendTitle(String title, String subtitle, int in, int stay, int out) {
        Player player = toPlayer();
        if (player != null) {
            Title t = Title.title(ColorString.parse(title),
                    ColorString.parse(subtitle),
                    Title.Times.of(Duration.ofMillis(in * 50L), Duration.ofMillis(stay * 50L), Duration.ofMillis(out * 50L)));
            player.showTitle(t);
        }
    }

    public String getName() {
        Player player = toPlayer();
        return player != null ? player.getName() : uniqueId.toString();
    }

    @Override
    public String toString() {
        Player player = toPlayer();
        String name = player != null ? "name=" + player.getName() + ", " : "";
        return "User{" + name + uniqueId + "}";
    }

}


