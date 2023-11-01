package org.caramel.backas.noah.listener;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.api.user.User;
import org.caramel.backas.noah.api.user.model.CurrentGameData;
import org.caramel.backas.noah.util.ChannelUtil;
import org.caramel.backas.noah.util.ColorString;
import org.caramel.backas.noah.util.TimeUtil;

import java.io.IOException;

public class UserListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.getPlayer().setGlowing(false);
        User user = User.get(e.getPlayer());
        user.setNeedUnload(false);
        e.getPlayer().setMaxHealth(100);
        e.getPlayer().setHealthScale(20);
        if (!e.getPlayer().isOp()) e.getPlayer().setGameMode(GameMode.ADVENTURE);
        Bukkit.getScheduler().runTaskAsynchronously(Noah.getInstance(), () -> {
            try {
                if (!user.isLoad()) user.loadData();
                if (user.isInGame()) return;
                Bukkit.getScheduler().runTask(Noah.getInstance(), () -> {
                    user.teleportAsync(Noah.getLobby().getSpawn());
                    e.getPlayer().setHealth(e.getPlayer().getMaxHealth());
                });
                Player player = user.toPlayer();
                if (player == null) return;
                player.getInventory().clear();
                ChannelUtil.giveItem(player);
            } catch (IOException ex) {
                Noah.getInstance().getDebugLogger().error("유저 데이터 로딩 중 오류 발생 : " + ex + " - " + user);
                Player player = user.toPlayer();
                if (player != null && player.isOnline()) {
                    Bukkit.getScheduler().runTask(Noah.getInstance(), () -> {
                        player.kick(ColorString.parse("<red>데이터 로딩 중 오류가 발생했습니다\n<yellow>디스코드를 통하여 관리자 또는 오류 신고 채널에 스크린샷과 함께 문의해주세요\n\n<gray>" + TimeUtil.formatNow()));
                    });
                }
                user.setNeedUnload(true);
            }
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        User user = User.get(e.getPlayer());
        if (user.getGameData() != null) return;
        user.setNeedUnload(true);
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        if (e.getSlot() == 7 && e.getWhoClicked().getGameMode() != GameMode.CREATIVE) {
            e.setCancelled(true);
        }
    }
}
