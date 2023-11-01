package org.caramel.backas.noah.command;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import moe.caramel.daydream.brigadier.AbstractCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.caramel.backas.noah.kda.KDA;
import org.caramel.backas.noah.user.User;
import org.jetbrains.annotations.NotNull;

public class KDACommand extends AbstractCommand {

    public KDACommand() {
        this.setAliases("kd", "킬뎃");
    }

    @Override
    public void createCommand(@NotNull LiteralArgumentBuilder<BukkitBrigadierCommandSource> builder) {
        builder.executes(context -> {
            if (context.getSource().getBukkitSender() instanceof Player player) {
                User user = User.get(player);
                player.sendMessage(Component.text("                                ", NamedTextColor.GOLD, TextDecoration.STRIKETHROUGH));
                player.sendMessage(Component.text().append(
                        Component.text(player.getName(), NamedTextColor.YELLOW),
                        Component.text("님의 KDA 정보 입니다.", NamedTextColor.WHITE)
                ));
                player.sendMessage(Component.empty());
                user.getDataContainer().getOrLoadAsync(KDA.class).thenApply(kda -> {
                    player.sendMessage(Component.text().append(
                            Component.text("  "),
                            Component.text("K/D", NamedTextColor.RED, TextDecoration.BOLD, TextDecoration.UNDERLINED),
                            Component.text(String.format(" %.2f", kda.deaths == 0 ? 0 : (double) kda.kills / kda.deaths))
                    ));
                    player.sendMessage(Component.text().append(
                            Component.text("  "),
                            Component.text("K/D/A", NamedTextColor.RED, TextDecoration.BOLD, TextDecoration.UNDERLINED),
                            Component.text(String.format(" %.2f", kda.deaths == 0 ? 0 :((double) kda.kills + kda.assists) / kda.deaths))
                    ));
                    player.sendMessage(Component.empty());
                    player.sendMessage(Component.text().append(
                            Component.text(" K", NamedTextColor.YELLOW, TextDecoration.BOLD),
                            Component.text("ills", NamedTextColor.GRAY),
                            Component.text(" >> ", NamedTextColor.DARK_GRAY),
                            Component.text(kda.kills)
                    ));
                    player.sendMessage(Component.text().append(
                            Component.text(" D", NamedTextColor.YELLOW, TextDecoration.BOLD),
                            Component.text("eaths", NamedTextColor.GRAY),
                            Component.text(" >> ", NamedTextColor.DARK_GRAY),
                            Component.text(kda.deaths)
                    ));
                    player.sendMessage(Component.text().append(
                            Component.text(" A", NamedTextColor.YELLOW, TextDecoration.BOLD),
                            Component.text("ssists", NamedTextColor.GRAY),
                            Component.text(" >> ", NamedTextColor.DARK_GRAY),
                            Component.text(kda.assists)
                    ));
                    player.sendMessage(Component.text("                                ", NamedTextColor.GOLD, TextDecoration.STRIKETHROUGH));
                    return kda;
                });
            }
            return 0;
        });
    }
}
