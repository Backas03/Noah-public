package org.caramel.backas.noah.util;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.caramel.backas.noah.Noah;

public class KillLogger {

    public static void display(Player player, Component message) {

        StringBuilder stringBuilder = new StringBuilder();

        String stripMsg = ((TextComponent) message).content();
        stringBuilder.append(" ".repeat(Math.max(0, (59 - stripMsg.length() - (stripMsg.replaceAll("[^ㄱ-힣]", "").length()
                + (stripMsg.contains("▄") ? 4 : 0) + (stripMsg.contains("▬▬") ? 2 : 0))))));

        /*
        String stripMsg = ChatColor.stripColor(msg);
        stringBuilder.append(" ".repeat(Math.max(0, (105 - stripMsg.length() - (stripMsg.replaceAll("[^ㄱ-힣]", "").length()
                + (stripMsg.contains("▄") ? 4 : 0) + (stripMsg.contains("▬▬") ? 2 : 0))))));
        stringBuilder.append(msg);
         */

        BossBar bossBar = BossBar.bossBar(
                Component.text(stringBuilder.toString()).append(message),
                1,
                BossBar.Color.WHITE,
                BossBar.Overlay.PROGRESS
        );

        player.showBossBar(bossBar);

        Bukkit.getScheduler().runTaskLaterAsynchronously(
                Noah.getInstance(),
                () -> player.hideBossBar(bossBar),
                100L
        );
    }
}
