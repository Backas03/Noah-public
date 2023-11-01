package org.caramel.backas.noah.advancement;

import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.api.event.ParticipantKillEnemyEvent;

public class AdvancementListener implements Listener {

    @EventHandler
    public void onKill(ParticipantKillEnemyEvent event) {
        event.getAttacker().getUser().getPlayer().ifPresent(player -> {
            AdvancementManager manager = Noah.getInstance().getAdvancementManager();
            AdvancementConstant[] constants = {
                    manager.getConstant(AdvancementKeys.KILLS_100),
                    manager.getConstant(AdvancementKeys.KILLS_500),
                    manager.getConstant(AdvancementKeys.KILLS_1000),
                    manager.getConstant(AdvancementKeys.KILLS_2000)
            };
            for (AdvancementConstant constant : constants) {
                Advancement advancement = constant.getAdvancement();
                AdvancementProgress progress = player.getAdvancementProgress(advancement);
                if (!progress.isDone()) {
                    progress.increaseCount();
                }
            }
        });
    }

    @EventHandler
    public void onClear(PlayerAdvancementDoneEvent event) {
        String key = event.getAdvancement().getKey().getKey();
        AdvancementManager manager = Noah.getInstance().getAdvancementManager();
        AdvancementConstant constant = manager.getConstant(key);
        Player player = event.getPlayer();
        if (constant != null) {
            if (constant.getAward() != null){
                constant.getAward().give(player, constant);
            }
            if (key.equals(AdvancementKeys.ADVANCEMENT_ARCHIVE_COUNT_5)) return;
            if (key.equals(AdvancementKeys.ADVANCEMENT_ARCHIVE_COUNT_10)) return;
            if (key.equals(AdvancementKeys.ADVANCEMENT_ARCHIVE_COUNT_15)) return;
            if (key.equals(AdvancementKeys.ADVANCEMENT_ARCHIVE_COUNT_20)) return;
            AdvancementConstant[] constants = {
                    manager.getConstant(AdvancementKeys.ADVANCEMENT_ARCHIVE_COUNT_5),
                    manager.getConstant(AdvancementKeys.ADVANCEMENT_ARCHIVE_COUNT_10),
                    manager.getConstant(AdvancementKeys.ADVANCEMENT_ARCHIVE_COUNT_15),
                    manager.getConstant(AdvancementKeys.ADVANCEMENT_ARCHIVE_COUNT_20),
            };
            for (AdvancementConstant c : constants) {
                AdvancementProgress progress = player.getAdvancementProgress(c.getAdvancement());
                if (!progress.isDone()) {
                    progress.increaseCount();
                }
            }
        }
    }

}
