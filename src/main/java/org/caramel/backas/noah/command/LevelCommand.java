package org.caramel.backas.noah.command;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import moe.caramel.daydream.brigadier.AbstractCommand;
import org.bukkit.entity.Player;
import org.caramel.backas.noah.level.LevelManager;
import org.caramel.backas.noah.user.User;
import org.jetbrains.annotations.NotNull;

public final class LevelCommand extends AbstractCommand {

    public LevelCommand() {
        this.setAliases("레벨");
    }

    @Override
    public void createCommand(@NotNull LiteralArgumentBuilder<BukkitBrigadierCommandSource> builder) {
        builder.executes(context -> {
            // Only Player
            if (!(context.getSource().getBukkitSender() instanceof Player player)) return -1;

            // Logic
            LevelManager.printInfo(User.get(player));
            return 0;
        });
    }
}
