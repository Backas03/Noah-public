package org.caramel.backas.noah.command;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import moe.caramel.daydream.brigadier.AbstractCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.caramel.backas.noah.prefix.Prefix;
import org.caramel.backas.noah.rating.TDMEloRating;
import org.caramel.backas.noah.user.User;
import org.jetbrains.annotations.NotNull;

public class RatingCommand extends AbstractCommand {

    public RatingCommand() {
        this.setAliases("rating", "레이팅");
    }

    @Override
    public void createCommand(@NotNull LiteralArgumentBuilder<BukkitBrigadierCommandSource> builder) {
        builder.executes(context -> {
            if (!(context.getSource().getBukkitSender() instanceof Player player)) return -1;
            User user = User.get(player);
            TDMEloRating rating = user.getDataContainer().get(TDMEloRating.class);

            player.sendMessage(Component.text().append(
                    Prefix.INFO_WITH_SPACE,
                    Component.text(player.getName(), NamedTextColor.YELLOW),
                    Component.text("님의", NamedTextColor.WHITE),
                    Component.text(" 팀 데스매치 ", NamedTextColor.GOLD),
                    Component.text("MMR 은 ", NamedTextColor.WHITE),
                    Component.text(rating.rating, NamedTextColor.AQUA),
                    Component.text("점 입니다.", NamedTextColor.WHITE)
            ));
            return 0;
        });
    }
}
