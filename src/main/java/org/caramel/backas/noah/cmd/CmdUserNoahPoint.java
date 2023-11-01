package org.caramel.backas.noah.cmd;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import moe.caramel.daydream.brigadier.AbstractCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.caramel.backas.noah.api.user.User;
import org.jetbrains.annotations.NotNull;

public class CmdUserNoahPoint extends AbstractCommand {

    public CmdUserNoahPoint() {
        setAliases("np");
    }

    @Override
    public void createCommand(@NotNull LiteralArgumentBuilder<BukkitBrigadierCommandSource> builder) {
        builder.executes(context -> {
            if (context.getSource().getBukkitSender() instanceof Player player) {
                User user = User.get(player);
                if (!user.isLoad()) {
                    user.sendMessage(Component.text()
                            .content("데이터가 로딩 중 입니다. 잠시만 기다려주세요.")
                            .color(NamedTextColor.RED)
                            .build());
                    return 1;
                }
                user.sendMessage(Component.text()
                        .content(player.getName())
                        .color(TextColor.color(92, 209, 229))
                        .append(Component.text()
                                .content("님의 ")
                                .color(NamedTextColor.WHITE))
                        .append(Component.text()
                                .content("Noah-Point")
                                .color(TextColor.color(209, 178, 255)))
                        .append(Component.text()
                                .content("는 ")
                                .color(NamedTextColor.WHITE))
                        .append(Component.text()
                                .content(String.valueOf(user.getNoahPoint().get()))
                                .color(TextColor.color(255, 167, 167)))
                        .append(Component.text()
                                .content(" 입니다.")
                                .color(NamedTextColor.WHITE))
                        .build()
                );
                return 0;
            }
            return 1;
        });
    }

}
