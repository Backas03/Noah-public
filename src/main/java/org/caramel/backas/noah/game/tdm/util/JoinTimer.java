package org.caramel.backas.noah.game.tdm.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.game.tdm.TDMGameOption;
import org.caramel.backas.noah.util.TitleBuilder;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JoinTimer {

    public static final Map<UUID, JoinTimer> RUNNING = new HashMap<>();

    private final BukkitTask timer;
    private final BukkitTask later;

    private int remained;

    public static JoinTimer start(Player player, Runnable callback) {
        return new JoinTimer(player, callback);
    }

    private JoinTimer(Player player, Runnable callback) {

        int seconds = TDMGameOption.JOIN_TIME;

        this.remained = seconds;
        timer = Bukkit.getScheduler().runTaskTimer(Noah.getInstance(), () -> {
            player.showTitle(
                    Title.title(
                            Component.text(remained + "초", NamedTextColor.GREEN),
                            Component.text("게임 입장까지", NamedTextColor.GRAY),
                            Title.Times.times(
                                    Duration.ZERO,
                                    Duration.ofSeconds(2),
                                    Duration.ZERO
                            )
                    )
            );
            remained--;
        }, 0L, 20L);
        later = Bukkit.getScheduler().runTaskLater(Noah.getInstance(), () -> {
            timer.cancel();
            player.clearTitle();
            callback.run();
            RUNNING.remove(player.getUniqueId());
        }, 20L * seconds);

        RUNNING.put(player.getUniqueId(), this);
    }

    public void cancel() {
        later.cancel();
        timer.cancel();
    }

    public static void cancelAll() {
        RUNNING.forEach((k, v) -> {
            Player player = Bukkit.getPlayer(k);
            if (player != null) {
                player.showTitle(
                        TitleBuilder.zeroInAndOut()
                                .setTitle(Component.text("게임 입장 불가", NamedTextColor.RED))
                                .setSubTitle(Component.text("게임이 종료되어 입장이 취소되었습니다", NamedTextColor.GRAY))
                                .build()
                );
            }
            v.cancel();
        });
        RUNNING.clear();
    }
}
