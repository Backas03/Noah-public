package org.caramel.backas.noah.command;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import moe.caramel.daydream.brigadier.AbstractCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.caramel.backas.noah.tier.TierManager;
import org.jetbrains.annotations.NotNull;

public final class TierCommand extends AbstractCommand {

    public TierCommand() {
        this.setAliases("티어");
    }

    @Override
    public void createCommand(@NotNull LiteralArgumentBuilder<BukkitBrigadierCommandSource> builder) {
        builder.executes(context -> {
            // Only Player
            if (!(context.getSource().getBukkitSender() instanceof Player player)) return -1;

            // Logic
            player.sendMessage(Component.text().append(
                Component.text(player.getName(), NamedTextColor.YELLOW),
                Component.text(" 님의 티어 스코어는 ", NamedTextColor.WHITE),
                Component.text(TierManager.getScore(player.getUniqueId()), NamedTextColor.GREEN),
                Component.text(" 입니다.", NamedTextColor.WHITE)
            ));
            return 0;
        });
    }
}
