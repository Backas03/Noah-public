package org.caramel.backas.noah.cmd;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import moe.caramel.daydream.brigadier.AbstractCommand;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.api.game.AbstractGame;
import org.caramel.backas.noah.api.game.GameManager;
import org.caramel.backas.noah.api.tier.Ranker;
import org.caramel.backas.noah.game.teamdeathmatch.TDMGame;
import org.caramel.backas.noah.util.OfflinePlayerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

public class CmdUserRanking extends AbstractCommand {

    public static final String ARG_GAME_TYPE = "게임 타입";

    private SuggestionProvider<BukkitBrigadierCommandSource> getGameList() {
        return this.mutableSuggestCollection(list -> {
            list.add("\"" + GameManager.getInstance().getGame(TDMGame.class).getName() + "\"");
            return list;
        });
    }

    @Override
    public void createCommand(@NotNull LiteralArgumentBuilder<BukkitBrigadierCommandSource> builder) {
        builder.executes(context -> {
            context.getSource().getBukkitSender().sendMessage("/ranking (game type)");
            return 0;
        });
        builder.then(
                this.argument(ARG_GAME_TYPE, StringArgumentType.string()).suggests(getGameList())
                        .executes(context -> {
                            String gameName = StringArgumentType.getString(context, ARG_GAME_TYPE);
                            AbstractGame game = GameManager.getInstance().getGame(gameName);
                            if (game == null) return 1;
                            Class<? extends AbstractGame> gameType = game.getClass();
                            LinkedList<Ranker> rankers = Noah.getInstance().getRankingData().getRankers(gameType);
                            CommandSender sender = context.getSource().getBukkitSender();
                            sender.sendMessage(gameName +  " TOP " + Noah.getInstance().getRankingData().getCashingSize(gameType));
                            for (int i=0; i<5; i++) {
                                String msg = "없음(미정)";
                                if (rankers.size() > i) {
                                    Ranker ranker = rankers.get(i);
                                    msg = ranker.getName();
                                    if (msg == null) {
                                        msg = "알 수 없음";
                                    }
                                    msg = msg + " - " + ranker.getRating();
                                }
                                sender.sendMessage((i + 1) + ". " + msg);
                            }
                            return 0;
                        })
        );
    }
}
