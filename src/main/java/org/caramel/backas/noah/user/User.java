package org.caramel.backas.noah.user;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.api.event.UserLoadEvent;
import org.caramel.backas.noah.game.GameManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Getter
public class User {

    private static final Logger LOGGER = LoggerFactory.getLogger(User.class);

    public static void initInstanceManager(Plugin taskOwner, int seconds) { // please call once

        Bukkit.getScheduler().runTaskTimerAsynchronously(taskOwner,
                User::saveAll,
                seconds * 20L,
                seconds * 20L
        );

    }

    public static void saveAll() {
        LOGGER.info("start saving {} of user data...", USER_MAP.size());
        int failed = 0;
        int saved = 0;
        Set<UUID> removed = new HashSet<>();
        for (User user : USER_MAP.values()) {
            try {
                user.dataContainer.saveAll();
                saved++;
                Player player = user.getPlayer().orElse(null);
                // 유저 인스턴스를 언로드 해야하거나 게임중이 아닌데 유저가 오프라인 상태이면 인스턴스를 언로드 리스트에 추가합니다.
                if (user.unload || (!GameManager.getInstance().isGameStarted() && player == null)) {
                    removed.add(user.uniqueId);
                }

            } catch (IOException e) {
                failed++;
                removed.add(user.uniqueId);
                LOGGER.warn("user data save failed. enqueued to unload. user={}, exception={}", user, e);
            }
        }
        removed.forEach(USER_MAP::remove);
        LOGGER.info("...done ({} saved, {} save-failed, {} unloaded)", saved, failed, removed.size());
    }

    public static void forceSaveAll() {
        LOGGER.info("start saving {} of user data...", USER_MAP.size());
        int failed = 0;
        int saved = 0;
        for (User user : USER_MAP.values()) {
            try {
                user.dataContainer.saveAll();
                saved++;
            } catch (IOException e) {
                LOGGER.warn("user data save failed. user={}, exception={}", user, e);
            }
        }
        LOGGER.info("...done ({} saved, {} save-failed)", saved, failed);
    }

    private static final Map<UUID, User> USER_MAP = new HashMap<>();

    public static User get(UUID uuid) {
        return USER_MAP.get(uuid);
    }

    public static Optional<User> getOptional(UUID uuid) {
        return Optional.ofNullable(get(uuid));
    }

    public static User get(Player player) {
        return get(player.getUniqueId());
    }

    public static User getOrInit(OfflinePlayer offlinePlayer) {
        return getOrInit(offlinePlayer.getUniqueId(), offlinePlayer.getName());
    }

    public static User getOrInit(UUID uuid, @Nullable String name) {
        return USER_MAP.computeIfAbsent(uuid, k -> {
            User user = new User(uuid, name);
            Bukkit.getPluginManager().callEvent(new UserLoadEvent(user));
            return user;
        });
    }

    public static void forceUnload(UUID uuid) {
        USER_MAP.remove(uuid);
    }

    public static void init(UUID uuid, @Nullable String name) {
        User user = new User(uuid, name);
        USER_MAP.putIfAbsent(uuid, user);
        Bukkit.getScheduler().runTask(Noah.getInstance(), () -> Bukkit.getPluginManager().callEvent(new UserLoadEvent(user)));
    }

    public static void init(Player player) {
        init(player.getUniqueId(), player.getName());
    }

    private @Setter boolean unload;
    private final UUID uniqueId;
    private final UserDataContainer dataContainer;

    private String name;

    private User(Player player) {
        this(player.getUniqueId(), player.getName());
    }

    private User(UUID uuid, String name) {
        this.uniqueId = uuid;
        this.name = name;

        this.unload = false;
        this.dataContainer = new UserDataContainer(this);
        for (Class<? extends UserData> clazz : UserDataContainer.CACHED_LIST) { // call after this.dataContainer is initialized
            try {
                this.dataContainer.getOrLoad(clazz);
            } catch (Exception e) {
                LoggerFactory.getLogger(clazz).error("유저 데이터 로딩 중 오류가 발생했습니다.", e);
            }
        }
    }

    public void sendMessage(Component component) {
        getPlayer().ifPresent(player -> player.sendMessage(component));
    }

    public void sendMessage(String uncolored) {
        getPlayer().ifPresent(player -> player.sendMessage(Component.text(uncolored)));
    }

    public Optional<Player> getPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(uniqueId));
    }

    public String getName() {
        if (name == null) getPlayer().ifPresent(player -> name = player.getName());
        return name;
    }

    @Override
    public String toString() {
        return String.format(
                "User{uniqueId=%s, name=%s}",
                uniqueId.toString(),
                name
        );
    }
}
