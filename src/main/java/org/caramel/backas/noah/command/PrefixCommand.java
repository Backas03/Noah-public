package org.caramel.backas.noah.command;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import moe.caramel.daydream.brigadier.AbstractCommand;
import org.bukkit.entity.Player;
import org.caramel.backas.noah.prefix.gui.PrefixInventory;
import org.jetbrains.annotations.NotNull;

public class PrefixCommand extends AbstractCommand {

    public PrefixCommand() {
        this.setAliases("칭호", "cldgh");
    }

    @Override
    public void createCommand(@NotNull LiteralArgumentBuilder<BukkitBrigadierCommandSource> builder) {
        builder.executes(context -> {
            if (!(context.getSource().getBukkitSender() instanceof Player player)) return -1;
            PrefixInventory.open(player);
            return 0;
        });
    }
}
