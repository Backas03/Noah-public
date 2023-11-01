package org.caramel.backas.noah.command;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import kr.abins.noah.structure.classes.Practice;
import moe.caramel.daydream.brigadier.AbstractCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RangeExitCommand extends AbstractCommand {
    @Override
    public void createCommand(@NotNull LiteralArgumentBuilder<BukkitBrigadierCommandSource> builder) {
        builder.executes(context -> {
            if (!(context.getSource().getBukkitSender() instanceof Player player)) return 1;
            Practice.exit(player);
            return 0;
        });
    }
}
