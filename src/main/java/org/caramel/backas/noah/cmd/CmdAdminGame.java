package org.caramel.backas.noah.cmd;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import moe.caramel.daydream.brigadier.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.CommandSender;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.api.game.AbstractGame;
import org.caramel.backas.noah.api.game.GameChannel;
import org.caramel.backas.noah.api.game.GameManager;
import org.caramel.backas.noah.game.teamdeathmatch.TDMGame;
import org.caramel.backas.noah.util.ColorString;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

public class CmdAdminGame extends AbstractCommand {

    public static final String FORCE_GAME_OVER_GAME_NAME = "force_game_over_game";
    public static final String FORCE_GAME_OVER_CHANNEL_NAME = "force_game_over_channel";

    public CmdAdminGame() {
        this.setPermission("game.command");
    }

    private void help(CommandSender sender)  {
        sender.sendMessage("/게임관리 강제종료 \"게임 이름\" \"채널 이름\"");
    }

    private SuggestionProvider<BukkitBrigadierCommandSource> getGameList() {
        return this.mutableSuggestCollection(list -> {
            for (AbstractGame game : GameManager.getInstance().getRegisteredGames()) list.add("\"" + game.getName() + "\"");
            return list;
        });
    }

    @Override
    public void createCommand(@NotNull LiteralArgumentBuilder<BukkitBrigadierCommandSource> builder) {
        builder.executes(context -> {
            help(context.getSource().getBukkitSender());
            return 0;
        });

        builder.then(
                this.literal("강제종료")
                        .then(this.argument(FORCE_GAME_OVER_GAME_NAME, StringArgumentType.string()).suggests(getGameList())
                                .then(this.argument(FORCE_GAME_OVER_CHANNEL_NAME, StringArgumentType.string()).suggests((context, builder1) -> {
                                    String gameName = StringArgumentType.getString(context, FORCE_GAME_OVER_GAME_NAME);
                                    AbstractGame game = GameManager.getInstance().getGame(gameName);
                                    if (game == null) return builder1.buildFuture();
                                    for (GameChannel<?> channel : game.getChannelPool().getChannels()) {
                                        if (!channel.isEmpty()) builder1.suggest("\"" + channel.getName() + "\"");
                                    }
                                    return builder1.buildFuture();
                                }).executes(context -> {
                                    CommandSender sender = context.getSource().getBukkitSender();
                                    String gameName = StringArgumentType.getString(context, FORCE_GAME_OVER_GAME_NAME);
                                    String channelName = StringArgumentType.getString(context, FORCE_GAME_OVER_CHANNEL_NAME);
                                    AbstractGame game = GameManager.getInstance().getGame(gameName);
                                    if (game == null) {
                                        sender.sendMessage("게임 이름 \"" + gameName + "\" 을(를) 찾을 수 없습니다.");
                                        return 1;
                                    }
                                    GameChannel<?> channel = game.getChannelPool().getByChannelName(channelName);
                                    if (channel == null) {
                                        sender.sendMessage("채널 이름 \"" + channelName + "\" 을(를) 찾을 수 없습니다.");
                                        return 1;
                                    }
                                    if (channel.isEmpty()) {
                                        sender.sendMessage("채널 \"" + channelName + "\" 은(는) 게임 중이 아닙니다.");
                                        return 1;
                                    }
                                    channel.getPlayers().keySet().forEach(
                                            party -> party.getAllMembers().forEach(
                                                    user -> user.sendMessage("<red>관리자에 의해 게임이 강제 종료되었습니다!")
                                            )
                                    );
                                    channel.over();
                                    return 0;
                                })
                        )
                )
        );
    }


}
