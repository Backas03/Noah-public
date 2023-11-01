package org.caramel.backas.noah.api.util;

import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.api.game.GameChannel;
import org.caramel.backas.noah.api.party.Party;
import org.caramel.backas.noah.api.user.User;
import org.caramel.backas.noah.util.ColorString;

public class KillLogger {

    public static void display(GameChannel<?> channel, String message) {
        BossBar bossBar = BossBar.bossBar(ColorString.parse(message), 1, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS);
        for (Party party : channel.getPlayers().keySet()) {
            for (User user : party.getAllMembers()) {
                Player player = user.toPlayer();
                if (player != null) {
                    player.showBossBar(bossBar);
                }
            }
        }
        Bukkit.getScheduler().runTaskLaterAsynchronously(Noah.getInstance(), () -> {
            for (Party party : channel.getPlayers().keySet()) {
                for (User user : party.getAllMembers()) {
                    Player player = user.toPlayer();
                    if (player != null) {
                        player.hideBossBar(bossBar);
                    }
                }
            }
        }, 100L);
    }


}
