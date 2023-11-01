package org.caramel.backas.noah.api.game.matching;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.api.game.AbstractGame;
import org.caramel.backas.noah.api.game.rating.EloRatingCalculator;
import org.caramel.backas.noah.api.party.Party;

import java.time.Duration;

@Getter
public class MatchingParty {

    private final Party party;
    private final int avgMMR;
    @Setter
    private int time;
    private BukkitTask timerTask;

    public MatchingParty(Party party, AbstractGame game) {
        this.party = party;
        this.avgMMR = EloRatingCalculator.getAvgMMR(party, game.getClass());
        this.time = 0;
        startTimer(game);
    }

    private void startTimer(AbstractGame game) {
        cancelTimer();
        time = 0;
        timerTask = Bukkit.getScheduler().runTaskTimer(Noah.getInstance(), () -> {
            party.sendTitle(Title.title(
                Component.text(game.getName(), NamedTextColor.YELLOW),
                Component.text().append(
                    Component.text("게임 매칭 중... ", NamedTextColor.GRAY),
                    Component.text(
                        Math.floorDiv(time, 60) + " : " + String.format("%02d", Math.floorMod(time, 60)),
                        NamedTextColor.AQUA
                    )
                ).build(),
                Title.Times.of(Duration.ofMillis(0), Duration.ofMillis(2000), Duration.ofMillis(0))
            ));
            time++;
        }, 0L, 20L);
    }

    public void cancelTimer() {
        if (timerTask == null) return;
        timerTask.cancel();
        timerTask = null;
    }



}
