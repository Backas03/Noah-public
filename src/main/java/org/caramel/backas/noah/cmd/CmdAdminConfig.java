package org.caramel.backas.noah.cmd;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import moe.caramel.daydream.brigadier.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.caramel.backas.noah.api.config.Config;
import org.caramel.backas.noah.api.game.AbstractGame;
import org.caramel.backas.noah.api.game.ConfigContainer;
import org.caramel.backas.noah.api.game.GameManager;
import org.caramel.backas.noah.util.ColorString;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class CmdAdminConfig extends AbstractCommand {

    public static final String ARG_GAME_TYPE = "게임 타입";
    public static final String ARG_OPTIONS = "옵션";
    public static final String ARG_VALUE_OPTION = "작업 타입";
    public static final String ARG_VALUE = "값";

    public CmdAdminConfig() {
        setPermission("op.op");
    }

    private SuggestionProvider<BukkitBrigadierCommandSource> getGameList() {
        return this.mutableSuggestCollection(list -> {
            for (AbstractGame game : GameManager.getInstance().getRegisteredGames()) {
                if (game instanceof ConfigContainer) {
                    list.add("\"" + game.getName() + "\"");
                }
            }
            return list;
        });
    }


    private CompletableFuture<Suggestions> getConfigValue(CommandContext<BukkitBrigadierCommandSource> context, SuggestionsBuilder builder) {
        String gameName = StringArgumentType.getString(context, ARG_GAME_TYPE);
        AbstractGame game = GameManager.getInstance().getGame(gameName);
        if (game instanceof ConfigContainer container) {
            Config config = container.getConfig();
            Class<? extends Config> clazz = config.getClass();
            for (Method method : clazz.getMethods()) {
                if (method.getReturnType() == Config.Value.class) {
                    builder.suggest(method.getName().replaceFirst("get", ""));
                }
            }
        }
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> getOptionValue(CommandContext<BukkitBrigadierCommandSource> context, SuggestionsBuilder builder) {
        String gameName = StringArgumentType.getString(context, ARG_GAME_TYPE);
        AbstractGame game = GameManager.getInstance().getGame(gameName);
        if (game instanceof ConfigContainer container) {
            Config config = container.getConfig();
            Class<? extends Config> clazz = config.getClass();
            String methodName = StringArgumentType.getString(context, ARG_OPTIONS);
            try {
                Method method = clazz.getMethod("get" + methodName);
                builder.suggest("Get");
                if (((Config.Value<?>) method.invoke(config)).get() instanceof Set) {
                    builder.suggest("Add");
                    builder.suggest("Remove");
                } else {
                    builder.suggest("Set");
                }
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignore) {

            }
        }
        return builder.buildFuture();
    }

    private void sendMessage(CommandContext<BukkitBrigadierCommandSource> context, String msg) {
        context.getSource().getBukkitSender().sendMessage(String.valueOf(msg));
    }

    private int executeGet(CommandContext<BukkitBrigadierCommandSource> context) {
        String gameName = StringArgumentType.getString(context, ARG_GAME_TYPE);
        AbstractGame game = GameManager.getInstance().getGame(gameName);
        if (game instanceof ConfigContainer container) {
            Config config = container.getConfig();
            Class<? extends Config> clazz = config.getClass();
            String methodName = StringArgumentType.getString(context, ARG_OPTIONS);
            try {
                Method method = clazz.getMethod("get" + methodName);
                Config.Value<?> valueInstance = (Config.Value<?>) method.invoke(config);
                sendMessage(context, String.valueOf(valueInstance.get()));
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignore) {
                return 1;
            }
            return 0;
        }
        return 1;
    }

    private int executeElse(CommandContext<BukkitBrigadierCommandSource> context) {
        String gameName = StringArgumentType.getString(context, ARG_GAME_TYPE);
        AbstractGame game = GameManager.getInstance().getGame(gameName);
        if (game instanceof ConfigContainer container) {
            Config config = container.getConfig();
            Class<? extends Config> clazz = config.getClass();
            String methodName = StringArgumentType.getString(context, ARG_OPTIONS);
            try {
                Method method = clazz.getMethod("get" + methodName);
                String option = StringArgumentType.getString(context, ARG_VALUE_OPTION);
                Config.Value<?> valueInstance = (Config.Value<?>) method.invoke(config);
                if (option.equals("Set")) {
                    String newValue = StringArgumentType.getString(context, ARG_VALUE);
                    if (valueInstance.get() instanceof Integer) {
                        Config.Value<Integer> newValueInstance = (Config.Value<Integer>) valueInstance;
                        try {
                            int val = Integer.parseInt(newValue);
                            newValueInstance.set(val);
                        } catch (NumberFormatException e) {
                            sendMessage(context, "올바르지 않은 int 값 > " + newValue);
                            return 1;
                        }
                        sendMessage(context, "done");
                        return 0;
                    }
                    if (valueInstance.get() instanceof Double) {
                        Config.Value<Double> newValueInstance = (Config.Value<Double>) valueInstance;
                        try {
                            double val = Double.parseDouble(newValue);
                            newValueInstance.set(val);
                        } catch (NumberFormatException e) {
                            sendMessage(context, "올바르지 않은 double 값 > " + newValue);
                            return 1;
                        }
                        sendMessage(context, "done");
                        return 0;
                    }
                    if (valueInstance.get() instanceof String) {
                        Config.Value<String> newValueInstance = (Config.Value<String>) valueInstance;
                        newValueInstance.set(newValue);
                        sendMessage(context, "done");
                        return 0;
                    }
                    if (valueInstance.get() instanceof BarColor) {
                        Config.Value<BarColor> newValueInstance = (Config.Value<BarColor>) valueInstance;
                        newValueInstance.set(BarColor.valueOf(newValue));
                        sendMessage(context, "done");
                        return 0;
                    }
                    if (valueInstance.get() instanceof BarStyle) {
                        Config.Value<BarStyle> newValueInstance = (Config.Value<BarStyle>) valueInstance;
                        newValueInstance.set(BarStyle.valueOf(newValue));
                        sendMessage(context, "done");
                        return 0;
                    }
                    return 0;
                }
                if (option.equals("Remove")) {
                    if (valueInstance.get() instanceof Set) {
                        Config.Value<Set<String>> newValueInstance = (Config.Value<Set<String>>) valueInstance;
                        String newValue = StringArgumentType.getString(context, ARG_VALUE);
                        Set<String> value = newValueInstance.get();
                        value.remove(newValue);
                        newValueInstance.set(value);
                        sendMessage(context, "done");
                        return 0;
                    }
                }
                if (option.equals("Add")) {
                    if (valueInstance.get() instanceof Set) {
                        Config.Value<Set<String>> newValueInstance = (Config.Value<Set<String>>) valueInstance;
                        String newValue = StringArgumentType.getString(context, ARG_VALUE);
                        Set<String> value = newValueInstance.get();
                        value.add(newValue);
                        newValueInstance.set(value);
                        sendMessage(context, "done");
                        return 0;
                    }
                }
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignore) {
                return 1;
            }
        }
        sendMessage(context, "올바르지 않은 커맨드 형식입니다.");
        return 1;
    }

    @Override
    public void createCommand(@NotNull LiteralArgumentBuilder<BukkitBrigadierCommandSource> builder) {
        builder.executes(context -> {
            sendMessage(context, "/config (Game-Type) (options) (Get/Set/Add/Remove) (value)");
           return 0;
        });

        builder.then(
                this.literal("reload")
                        .then(
                                this.argument(ARG_GAME_TYPE, StringArgumentType.string())
                                        .suggests(getGameList())
                                        .executes(context -> {
                                            String gameName = StringArgumentType.getString(context, ARG_GAME_TYPE);
                                            AbstractGame game = GameManager.getInstance().getGame(gameName);
                                            if (game instanceof ConfigContainer container) {
                                                container.getConfig().load();
                                                sendMessage(context, "done");
                                            }
                                            return 0;
                                        })
                        )
        );

        builder.then(
                this.literal("save")
                        .then(
                                this.argument(ARG_GAME_TYPE, StringArgumentType.string())
                                        .suggests(getGameList())
                                        .executes(context -> {
                                            String gameName = StringArgumentType.getString(context, ARG_GAME_TYPE);
                                            AbstractGame game = GameManager.getInstance().getGame(gameName);
                                            if (game instanceof ConfigContainer container) {
                                                try {
                                                    container.getConfig().save();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                    sendMessage(context, "failed > " + e);
                                                    return 1;
                                                }
                                                sendMessage(context, "done");
                                            }
                                            return 0;
                                        })
                        )
        );

        builder.then(
                this.argument(ARG_GAME_TYPE, StringArgumentType.string())
                        .suggests(getGameList())
                        .then(
                                this.argument(ARG_OPTIONS, StringArgumentType.string())
                                        .suggests(this::getConfigValue)
                                        .then(
                                                this.argument(ARG_VALUE_OPTION, StringArgumentType.string())
                                                        .suggests(this::getOptionValue)
                                                        .executes(this::executeGet)
                                                        .then(
                                                                this.argument(ARG_VALUE, StringArgumentType.string())
                                                                        .executes(this::executeElse)
                                                        )
                                        )
                        )
        );
    }

}
