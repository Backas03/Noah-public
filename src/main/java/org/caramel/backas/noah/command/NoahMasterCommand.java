package org.caramel.backas.noah.command;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import moe.caramel.daydream.brigadier.AbstractCommand;
import moe.caramel.daydream.brigadier.Arguments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.game.GameException;
import org.caramel.backas.noah.game.GameManager;
import org.caramel.backas.noah.level.ExpData;
import org.caramel.backas.noah.level.Level;
import org.caramel.backas.noah.noahpoint.NoahPoint;
import org.caramel.backas.noah.prefix.Prefix;
import org.caramel.backas.noah.prefix.PrefixData;
import org.caramel.backas.noah.skin.Skin;
import org.caramel.backas.noah.skin.SkinData;
import org.caramel.backas.noah.skin.SkinException;
import org.caramel.backas.noah.skin.model.reaper.test.SkinReaperTest;
import org.caramel.backas.noah.skin.model.reaper.test.effect.EffectReaperTestReload;
import org.caramel.backas.noah.skin.model.reaper.test.effect.EffectReaperTestReloadEnd;
import org.caramel.backas.noah.skin.model.reaper.test.effect.EffectReaperTestShoot;
import org.caramel.backas.noah.user.User;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class NoahMasterCommand extends AbstractCommand {

    static final String NAME = "name", TARGET = "target", AMOUNT = "amount", NAMESPACED_KEY = "namespaced key", LEGACY_TEXT = "legacy text";

    public NoahMasterCommand() {
        this.setPermission("noah.gm");
        this.setDescription("관리자 전용 관리 명령어");
    }

    @Override
    public void createCommand(@NotNull LiteralArgumentBuilder<BukkitBrigadierCommandSource> builder) {

        // "force-start" sub command
        final Command<BukkitBrigadierCommandSource> forceStart = context -> {
            try {
                GameManager.getInstance().start();
            } catch (GameException exception) {
                if (context.getSource().getBukkitSender() instanceof Player player) {
                    player.sendMessage(exception.getComponent());
                } else {
                    exception.printStackTrace();
                }
            }
            return 0;
        };
        builder.then(this.literal("force-start").executes(forceStart));
        builder.then(this.literal("강제시작").executes(forceStart));


        // "force-stop" sub command
        final Command<BukkitBrigadierCommandSource> forceStop = context -> {
            try {
                GameManager.getInstance().over();
            } catch (GameException exception) {
                if (context.getSource().getBukkitSender() instanceof Player player) {
                    player.sendMessage(exception.getComponent());
                } else {
                    exception.printStackTrace();
                }
            }
            return 0;
        };
        builder.then(this.literal("force-stop").executes(forceStop));
        builder.then(this.literal("강제종료").executes(forceStop));


        // "set-exp" sub command
        builder.then(this.literal("set-exp")
               .then(this.argument(TARGET, Arguments.ENTITY.player())
               .then(this.argument(AMOUNT, IntegerArgumentType.integer(0, ExpData.getExperience(ExpData.MAX_LEVEL)))
               .executes(context -> {
                    final User user = User.get(Arguments.ENTITY.getPlayer(context, TARGET));
                    if (user == null) return 1;

                    user.getDataContainer().getOrLoadAsync(Level.class).thenApply(model -> {
                        int amount = IntegerArgumentType.getInteger(context, AMOUNT);
                        model.setExp(amount);
                        if (context.getSource().getBukkitSender() instanceof Player player) {
                            player.sendMessage("설정 완료");
                        }
                        user.sendMessage("관리자에 의해 경험치가 " + amount + "(으)로 설정 되었습니다.");
                        return model;
                    });
                    return 0;
               })))
        );


        // "npm" sub command
        builder.then(this.literal("npm")
               .executes(context -> {
                    CommandSender sender = context.getSource().getBukkitSender();
                    sender.sendMessage("/noah npm set [name] [amount]");
                    return 0;
               }).then(this.literal("set")
                 .then(this.argument(NAME, StringArgumentType.string())
                 .then(this.argument(AMOUNT, IntegerArgumentType.integer(0))
                 .executes(context -> {
                    final CommandSender sender = context.getSource().getBukkitSender();
                    final String name = StringArgumentType.getString(context, NAME);
                    final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(name);
                    if (offlinePlayer == null) {
                        sender.sendMessage("플레이어를 찾을 수 없습니다. name=" + name);
                        return 1;
                    }

                    final int amount = IntegerArgumentType.getInteger(context, AMOUNT);
                    final User user = User.getOrInit(offlinePlayer);
                    user.getDataContainer().getOrLoadAsync(NoahPoint.class).thenApply(noahPoint -> {
                        noahPoint.value = amount;
                        sender.sendMessage(name + "님의 Noah-Point 를 " + amount + " (으)로 설정하였습니다.");
                        return noahPoint;
                    });
                    return 0;
                 })))
               )
        );


        // "get-test-skin" sub command
        builder.then(this.literal("get-test-skin").executes(context -> {
            // Only Player
            if (!(context.getSource().getBukkitSender() instanceof Player player)) return -1;

            // Logic
            final User user = User.get(player);
            user.getDataContainer().getOrLoadAsync(SkinData.class).thenApply(skinData -> {
                try {
                    final Skin skin = Skin.find(SkinReaperTest.MODEL_KEY);
                    if (skin != null) {
                        if (!skinData.hasSkin(skin)) skinData.addSkin(skin);
                        skinData.grantEffect(skin, EffectReaperTestReload.EFFECT_ID, true);
                        skinData.grantEffect(skin, EffectReaperTestShoot.EFFECT_ID, true);
                        skinData.grantEffect(skin, EffectReaperTestReloadEnd.EFFECT_ID, true);
                        player.sendMessage("스킨 데이터 설정 완료");
                    }
                } catch (SkinException exception) {
                    player.sendMessage(exception.getComponent());
                }
                return skinData;
            });
            return 0;
        }));

        // "prefix" sub command
        builder.then(this.literal("prefix")
                .executes(context -> {
                    final CommandSender sender = context.getSource().getBukkitSender();
                    sender.sendMessage("/noah prefix create [namespacedKey] [prefix(legacyText)]");
                    sender.sendMessage("/noah prefix delete [namespacedKey]");
                    sender.sendMessage("/noah prefix player add [name] [namespacedKey]");
                    sender.sendMessage("/noah prefix player remove [name] [namespacedKey]");
                    sender.sendMessage("/noah prefix player list [name]");
                    sender.sendMessage("/noah prefix list");
                    sender.sendMessage("/noah prefix reload");
                    sender.sendMessage("/noah prefix save");
                    return 0;
                }).then(this.literal("create")
                  .then(this.argument(NAMESPACED_KEY, StringArgumentType.string())
                  .then(this.argument(LEGACY_TEXT, StringArgumentType.string())
                  .executes(context -> {
                      final CommandSender sender = context.getSource().getBukkitSender();
                      String namespacedKey = StringArgumentType.getString(context, NAMESPACED_KEY);
                      String legacyText = StringArgumentType.getString(context, LEGACY_TEXT);
                      Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(legacyText);
                      Prefix.create(namespacedKey, component);
                      sender.sendMessage(Component.text("칭호 생성 완료. namespacedKey: " + namespacedKey + ", content: ").append(component));
                      return 0;
                  }))))
                .then(this.literal("list").executes(context -> {
                    final CommandSender sender = context.getSource().getBukkitSender();
                    sender.sendMessage("생성된 칭호 목록 (namespacedKey - content)");
                    for (Map.Entry<String, Component> entry : Prefix.getRegistered().entrySet()) {
                        sender.sendMessage(Component.text(entry.getKey() + " - ").append(entry.getValue()));
                    }
                    return 0;
                }))
                .then(this.literal("reload").executes(context -> {
                    final CommandSender sender = context.getSource().getBukkitSender();
                    sender.sendMessage("칭호 데이터를 리로드합니다.");
                    Bukkit.getScheduler().runTaskAsynchronously(Noah.getInstance(), () -> {
                        try {
                            Prefix.load();
                            sender.sendMessage("칭호 데이터 리로드 완료.");
                        } catch (IOException e) {
                            e.printStackTrace();
                            sender.sendMessage("칭호 데이터를 리로드 하는 중 오류가 발생했습니다." + e.getMessage());
                        }
                    });
                    return 0;
                }))
                .then(this.literal("save").executes(context -> {
                    final CommandSender sender = context.getSource().getBukkitSender();
                    sender.sendMessage("칭호 데이터를 저장합니다.");
                    Bukkit.getScheduler().runTaskAsynchronously(Noah.getInstance(), () -> {
                        try {
                            Prefix.save();
                            sender.sendMessage("칭호 데이터 저장 완료.");
                        } catch (IOException e) {
                            e.printStackTrace();
                            sender.sendMessage("칭호 데이터를 저장 하는 중 오류가 발생했습니다." + e.getMessage());
                        }
                    });
                    return 0;
                }))
                .then(this.literal("delete")
                .then(this.argument(NAMESPACED_KEY, StringArgumentType.string())
                .executes(context -> {
                    final CommandSender sender = context.getSource().getBukkitSender();
                    String namespacedKey = StringArgumentType.getString(context, NAMESPACED_KEY);
                    if (!Prefix.containsKey(namespacedKey)) {
                        sender.sendMessage("칭호를 찾을 수 없습니다.");
                        return -1;
                    }
                    Component component = Prefix.getComponent(namespacedKey);
                    Prefix.delete(namespacedKey);
                    sender.sendMessage(Component.text("칭호 삭제 완료. namespacedKey: " + namespacedKey + ", content: ").append(component));
                    return 0;
                })))
                .then(this.literal("player")
                .then(this.literal("list")
                .then(this.argument(NAME, StringArgumentType.string()).suggests(offlinePlayerSuggestions())
                .executes(context -> {
                    final CommandSender sender = context.getSource().getBukkitSender();
                    String name = StringArgumentType.getString(context, NAME);
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(name);
                    if (offlinePlayer == null) {
                        sender.sendMessage("대상을 찾을 수 없습니다. (" + name + ")");
                        return -1;
                    }
                    sender.sendMessage(name + " 님의 칭호 데이터 입니다.");
                    User.getOrInit(offlinePlayer).getDataContainer().getOrLoadAsync(PrefixData.class).thenApply(prefixData -> {
                        sender.sendMessage("보유 중인 칭호 목록 (namespacedKey)");
                        sender.sendMessage(String.valueOf(prefixData.getNamespacedKeys()));
                        sender.sendMessage("");
                        sender.sendMessage("현재 착용중인 칭호");
                        if (prefixData.getCurrentPrefix() == null) sender.sendMessage("없음");
                        else {
                            sender.sendMessage(
                                    Component.text("namespacedKey: " + prefixData.getCurrentNamespacedKey() + ", content: ")
                                            .append(prefixData.getCurrentPrefix())
                            );
                        }
                        return prefixData;
                    });
                    return 0;
                })))
                .then(this.literal("add")
                .then(this.argument(NAME, StringArgumentType.string()).suggests(offlinePlayerSuggestions())
                .then(this.argument(NAMESPACED_KEY, StringArgumentType.string()).suggests(namespacedKeySuggestions())
                .executes(context -> {
                    final CommandSender sender = context.getSource().getBukkitSender();
                    String name = StringArgumentType.getString(context, NAME);
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(name);
                    if (offlinePlayer == null) {
                        sender.sendMessage("대상을 찾을 수 없습니다. (" + name + ")");
                        return -1;
                    }
                    String namespacedKey = StringArgumentType.getString(context, NAMESPACED_KEY);
                    if (!Prefix.containsKey(namespacedKey)) {
                        sender.sendMessage("칭호를 찾을 수 없습니다. (" + namespacedKey + ")");
                        return -1;
                    }
                    User.getOrInit(offlinePlayer).getDataContainer().getOrLoadAsync(PrefixData.class).thenApply(prefixData -> {
                        prefixData.addPrefix(namespacedKey);
                        sender.sendMessage("추가 완료.");
                        return prefixData;
                    });
                    return 0;
                }))))
                .then(this.literal("remove")
                .then(this.argument(NAME, StringArgumentType.string()).suggests(offlinePlayerSuggestions())
                .then(this.argument(NAMESPACED_KEY, StringArgumentType.string()).suggests(this::playerNamespacedKeySuggestions)
                .executes(context -> {
                    final CommandSender sender = context.getSource().getBukkitSender();
                    String name = StringArgumentType.getString(context, NAME);
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(name);
                    if (offlinePlayer == null) {
                        sender.sendMessage("대상을 찾을 수 없습니다. (" + name + ")");
                        return -1;
                    }
                    String namespacedKey = StringArgumentType.getString(context, NAMESPACED_KEY);
                    if (!Prefix.containsKey(namespacedKey)) {
                        sender.sendMessage("칭호를 찾을 수 없습니다. (" + namespacedKey + ")");
                        return -1;
                    }
                    User.getOrInit(offlinePlayer).getDataContainer().getOrLoadAsync(PrefixData.class).thenApply(prefixData -> {
                        prefixData.removePrefix(namespacedKey);
                        sender.sendMessage("제거 완료.");
                        return prefixData;
                    });
                    return 0;
                }))))
        ));
    }

    private CompletableFuture<Suggestions> playerNamespacedKeySuggestions(CommandContext<BukkitBrigadierCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(StringArgumentType.getString(context, NAME));
        if (offlinePlayer == null) {
            return Suggestions.empty();
        }
        return this.mutableSuggestCollection(list -> {
            list.addAll(User.get(offlinePlayer.getUniqueId()).getDataContainer().getOrLoad(PrefixData.class).getNamespacedKeys());
            return list;
        }).getSuggestions(context, builder);
    }

    private SuggestionProvider<BukkitBrigadierCommandSource> namespacedKeySuggestions() {
        return this.mutableSuggestCollection(list -> {
                    list.addAll(Prefix.getRegistered().keySet());
                    return list;
                });
    }

    private SuggestionProvider<BukkitBrigadierCommandSource> offlinePlayerSuggestions() {
        return this.mutableSuggestCollection(list -> {
            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) list.add(offlinePlayer.getName());
            return list;
        });
    }
}
