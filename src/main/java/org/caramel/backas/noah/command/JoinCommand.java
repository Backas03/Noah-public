package org.caramel.backas.noah.command;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import moe.caramel.daydream.brigadier.AbstractCommand;
import org.bukkit.entity.Player;
import org.caramel.backas.noah.game.GameManager;
import org.jetbrains.annotations.NotNull;

public final class JoinCommand extends AbstractCommand {

    public JoinCommand() {
        this.setAliases("참여");
    }

    @Override
    public void createCommand(@NotNull LiteralArgumentBuilder<BukkitBrigadierCommandSource> builder) {
        builder.executes(context -> {
            // Only Player
            if (!(context.getSource().getBukkitSender() instanceof Player player)) return -1;

            // Logic
            GameManager.getInstance().join(player);
            return 0;
        });
    }
}
