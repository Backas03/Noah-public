package org.caramel.backas.noah.cmd;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import moe.caramel.daydream.brigadier.AbstractCommand;
import org.bukkit.entity.Player;
import org.caramel.backas.noah.api.game.rating.Rating;
import org.caramel.backas.noah.api.tier.Tier;
import org.caramel.backas.noah.api.user.User;
import org.caramel.backas.noah.game.teamdeathmatch.TDMGame;
import org.jetbrains.annotations.NotNull;

public class CmdUserRating extends AbstractCommand {

    @Override
    public void createCommand(@NotNull LiteralArgumentBuilder<BukkitBrigadierCommandSource> builder) {
        builder.requires(source -> source.getBukkitSender() instanceof Player).executes(context -> {
            Player player = (Player) context.getSource().getBukkitSender();
            User user = User.get(player);
            if (!user.isLoad()) {
                user.sendMessage("<red>데이터가 로딩중입니다. 잠시만 기다려주세요.");
                return 1;
            }
            Rating rating = user.getRating(TDMGame.class);
            Tier tier = Tier.getTierByPoint(rating.get());
            user.sendMessage("팀 데스매치");
            user.sendMessage("    티어 : " + tier.getName() + " " + tier.getRating(rating.get()));
            user.sendMessage("    MMR : " + rating.get());
            return 0;
        });
    }


}
