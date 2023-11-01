package org.caramel.backas.noah.level;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.caramel.backas.noah.prefix.Prefix;
import org.caramel.backas.noah.user.User;
import java.util.concurrent.CompletableFuture;

public class LevelManager {

    public static void addExp(User user, int amount) {
        user.getDataContainer().getOrLoadAsync(Level.class).thenApply(model -> {

            if (model.isMaxLevel()) {
                /* 최대 레벨 이므로 exp 대신 다른 보상을 지급 할 것인가? */
                return model;
            }
            int beforeExp = model.getExp();
            int beforeLevel = model.getLevel();
            model.setExp(model.getExp() + amount);
            int afterLevel = model.getLevel();
            TextComponent component = Component
                    .text("[LEVEL] ", TextColor.color(170, 220, 130))
                    .append(Component.text("경험치를 획득하셨습니다. ", NamedTextColor.WHITE))
                    .append(Component.text(beforeExp, NamedTextColor.GRAY))
                    .append(Component.text(" -> ", NamedTextColor.DARK_GRAY))
                    .append(Component.text(" " + model.getExp(), NamedTextColor.YELLOW))
                    .append(Component.text(" (+" + amount + ")", NamedTextColor.GREEN))
                    .append(Component.text(" | ", NamedTextColor.DARK_GRAY));
            if (beforeLevel < afterLevel) {
                component = component
                        .append(Component.text("레벨 업! ", NamedTextColor.RED))
                        .append(Component.text("Lv." + beforeLevel, NamedTextColor.GRAY))
                        .append(Component.text(" -> ", NamedTextColor.DARK_GRAY))
                        .append(Component.text(afterLevel, NamedTextColor.GREEN));
                Bukkit.broadcast(Component.text().append(
                        Prefix.INFO_WITH_SPACE,
                        Component.text(user.getName(), NamedTextColor.YELLOW),
                        Component.text("님께서 ", NamedTextColor.WHITE),
                        Component.text(afterLevel + "레벨", NamedTextColor.GREEN),
                        Component.text("로 레벨 업 하였습니다.", NamedTextColor.WHITE)
                ).build());
            } else {
                component = component
                        .append(Component.text("Lv." + model.getLevel(), NamedTextColor.GRAY))
                        .append(Component.text(" -> ", NamedTextColor.DARK_GRAY))
                        .append(Component.text(model.getLevel() + 1, NamedTextColor.GREEN))
                        .append(Component.text(" 까지 ", NamedTextColor.WHITE))
                        .append(Component.text(
                                (ExpData.getExperience(model.getLevel() + 1)
                                        - model.getExp()) + " EXP ", NamedTextColor.AQUA))
                        .append(Component.text(" 남았습니다.", NamedTextColor.WHITE));
            }
            final TextComponent fc = component;
            printInfo(user).thenApply(level -> {
                user.sendMessage(fc);
                return level;
            });
            return model;
        });
    }

    public static CompletableFuture<Level> printInfo(User user) {
        return user.getDataContainer().getOrLoadAsync(Level.class).thenApply(model -> {
            user.sendMessage(
                    Component.text().append(
                            Component.text("  * ", NamedTextColor.RED),
                            Component.text(user.getName(), NamedTextColor.YELLOW),
                            Component.text(" 님의 정보", NamedTextColor.WHITE),
                            Component.text(" *", NamedTextColor.RED)
                    ).build()
            );
            user.sendMessage(
                    Component.text().append(
                            Component.text(" Lv. ", NamedTextColor.GRAY),
                            Component.text(model.getLevel(), NamedTextColor.WHITE).decorate(TextDecoration.UNDERLINED),
                            Component.text("    (max-Lv. ", NamedTextColor.DARK_GRAY),
                            Component.text(ExpData.MAX_LEVEL, NamedTextColor.GRAY),
                            Component.text(")", NamedTextColor.DARK_GRAY)
                    ).build()
            );
            if (!model.isMaxLevel()) {
                /* TODO: replace with resource */
                int currentExp = ExpData.getExperience(model.getLevel() + 1) - model.getExp();
                int nextLevelExp = ExpData.getExperience(model.getLevel() + 1) - ExpData.getExperience(model.getLevel());
                int target = (int) (Math.floor((float) currentExp / nextLevelExp * 50));
                Component progress = Component.text("    ");
                for (int i=50; i>0; i--) {
                    Component component = Component.text("|");
                    if (i <= target) {
                        progress = progress.append(component.color(NamedTextColor.GRAY));
                        continue;
                    }
                    progress = progress.append(component.color(NamedTextColor.GREEN));
                }
                user.sendMessage(
                        Component.text().append(
                                Component.text(" Experience. ", NamedTextColor.AQUA),
                                Component.text(model.getExp(), NamedTextColor.WHITE),
                                Component.text("/" + ExpData.getExperience(model.getLevel() + 1), NamedTextColor.GRAY)
                        ).build()
                );
                double percentage = Math.floor((float) (model.getExp() - ExpData.getExperience(model.getLevel())) / nextLevelExp * 100);
                progress = progress.append(Component.text(String.format("  (%.2f%%)", percentage), NamedTextColor.YELLOW));
                user.sendMessage(progress);
            }
            return model;
        });
    }
}
