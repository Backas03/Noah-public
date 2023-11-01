package org.caramel.backas.noah.command;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import moe.caramel.daydream.brigadier.AbstractCommand;
import org.bukkit.entity.Player;
import org.caramel.backas.noah.noahpoint.NoahPoint;
import org.caramel.backas.noah.user.User;
import org.jetbrains.annotations.NotNull;

public final class NoahPointCommand extends AbstractCommand {

    public NoahPointCommand() {
        this.setAliases("np", "noahpoint");
    }

    @Override
    public void createCommand(@NotNull LiteralArgumentBuilder<BukkitBrigadierCommandSource> builder) {
        builder.executes(context -> {
            // Only Player
            if (!(context.getSource().getBukkitSender() instanceof Player player)) return -1;

            // Logic
            final User user = User.get(player);
            user.getDataContainer().getOrLoadAsync(NoahPoint.class).thenApply(noahPoint -> {
                player.sendMessage(player.getName() + "님의 Noah-Point 는 " + noahPoint.value + " 입니다.");
                return noahPoint;
            });
            return 0;
        });
    }
}
