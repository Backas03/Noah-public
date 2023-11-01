package org.caramel.backas.noah.command;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import moe.caramel.daydream.brigadier.AbstractCommand;
import org.bukkit.entity.Player;
import org.caramel.backas.noah.skin.gui.SkinInventory;
import org.jetbrains.annotations.NotNull;

public final class SkinCommand extends AbstractCommand {

    public SkinCommand() {
        this.setAliases("스킨", "skins");
    }

    @Override
    public void createCommand(@NotNull LiteralArgumentBuilder<BukkitBrigadierCommandSource> builder) {
        builder.executes(context -> {
            // Only Player
            if (!(context.getSource().getBukkitSender() instanceof Player player)) return -1;

            // Logic
            SkinInventory.open(player);
            return 0;
        });
    }
}
