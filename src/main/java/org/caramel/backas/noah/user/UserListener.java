package org.caramel.backas.noah.user;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import io.papermc.paper.event.player.AsyncChatEvent;
import kr.abins.noah.structure.classes.Practice;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.caramel.backas.noah.Lobby;
import org.caramel.backas.noah.game.GameManager;
import org.caramel.backas.noah.game.ScoreboardManager;
import org.caramel.backas.noah.level.Level;
import org.caramel.backas.noah.prefix.Prefix;
import org.caramel.backas.noah.prefix.PrefixData;
import org.caramel.backas.noah.ui.InteractableHologram;
import org.caramel.backas.noah.util.ChannelItemUtil;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.UUID;

public class UserListener implements Listener {

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        if (User.get(uuid) == null) {
            User.init(uuid, event.getName());
        }
        User.get(uuid).setUnload(false);
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            User.forceUnload(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        ScoreboardManager.activateScoreboard(player);
        if (!GameManager.getInstance().isInGame(player.getUniqueId())) {
            ChannelItemUtil.giveItem(player);
        }
    }

    @Deprecated // 더 나은 API가 언젠가 나오
    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(final AsyncChatEvent event) {
        final ChatRenderer prevRenderer = event.renderer();
        final Level level = User.get(event.getPlayer()).getDataContainer().getOrLoad(Level.class); // ...
        final PrefixData prefixData = User.get(event.getPlayer()).getDataContainer().getOrLoad(PrefixData.class);
        final String namespacedKey = prefixData.getCurrentNamespacedKey();
        if (namespacedKey != null && !Prefix.containsKey(namespacedKey)) { // 칭호 존재여부 확인
            prefixData.removePrefix(namespacedKey);
        }
        if (prefixData.getCurrentPrefix() == null) { // 칭호 없음
            event.renderer((source, sourceDisplayName, message, viewer) -> Component.text().append(
                Component.text("Lv." + level.getLevel() + " ", NamedTextColor.GRAY),
                prevRenderer.render(source, sourceDisplayName, message, viewer)
            ).build());
        } else { // 칭호 있음
            event.renderer((source, sourceDisplayName, message, viewer) -> Component.text().append(
                Component.text().append(prefixData.getCurrentPrefix(), Component.space()),
                Component.text("Lv." + level.getLevel() + " ", NamedTextColor.GRAY),
                prevRenderer.render(source, sourceDisplayName, message, viewer)
            ).build());
        }
    }

    @EventHandler
    public void onSpawn(PlayerSpawnLocationEvent event) {
        /* 인게임 상태가 아니라면 스폰으로 텔레포트합니다. */
        if (!GameManager.getInstance().isInGame(event.getPlayer())) {
            event.setSpawnLocation(Lobby.getSpawnPoint());
        }
    }

    @EventHandler
    public void onChunkLoad(PlayerChunkLoadEvent event) {
        if (event.getWorld() == Lobby.getWorld() && !InteractableHologram.Info.hasCreated(event.getPlayer())) {
            InteractableHologram.Info.create(event.getPlayer()); // 정보 홀로그램 생성
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        /*
         게임이 시작하지 않았을 시 요놈이 유저 데이터를 관리합니다.
         시작된 게임이 있다면 그 게임에서 유저 데이터를 언로딩 할지 관리합니다.
         */
        if (!GameManager.getInstance().isGameStarted()) {
            User.get(event.getPlayer()).setUnload(true);
            GameManager.getInstance().getVote().remove(event.getPlayer().getUniqueId());
            event.getPlayer().teleport(Lobby.getSpawnPoint());
        } else {
            GameManager.getInstance().getVote().remove(event.getPlayer().getUniqueId(), false);
        }

        InteractableHologram.Info.remove(event.getPlayer()); // 정보 홀로그램 제거

        if (Practice.isInPractice(event.getPlayer()).getFirst()) {
            Practice.exit(event.getPlayer());
        }
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        if (event.getPlayer().getWorld() == Lobby.getWorld()) {
            InteractableHologram.Info.spawn(event.getPlayer());
        }
    }
}
