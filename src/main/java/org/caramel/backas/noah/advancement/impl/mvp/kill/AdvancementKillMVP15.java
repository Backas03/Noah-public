package org.caramel.backas.noah.advancement.impl.mvp.kill;

import io.papermc.paper.advancement.AdvancementDisplay;
import moe.caramel.daydream.advancement.AdvancementBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.advancement.AdvancementAward;
import org.caramel.backas.noah.advancement.AdvancementData;
import org.caramel.backas.noah.advancement.AdvancementKeys;
import org.caramel.backas.noah.prefix.Prefix;
import org.caramel.backas.noah.prefix.PrefixData;
import org.caramel.backas.noah.user.User;
import org.caramel.backas.noah.util.ItemStackUtil;

import java.util.function.Consumer;

public class AdvancementKillMVP15 implements AdvancementData {

    public static final String PREFIX_KEY = "지휘관";

    @Override
    public String getKey() {
        return AdvancementKeys.MVP_KILLS_15;
    }

    @Override
    public Consumer<AdvancementBuilder> getBuilder() {
        return builder -> {
            // Reaper icon
            ItemStack icon = new ItemStack(Material.LARGE_AMETHYST_BUD);
            Component prefix = Prefix.getComponent(PREFIX_KEY);

            builder.display(
                    icon,
                    Component.text(PREFIX_KEY, prefix.color()),
                    Component.text().append(
                            Component.text("게임 종료시인원", NamedTextColor.WHITE),
                            Component.text(" 6명 이상인 게임", NamedTextColor.YELLOW),
                            Component.text("에서", NamedTextColor.WHITE),
                            Component.text(" Kill MVP 15번", NamedTextColor.RED),
                            Component.text("을 달성하세요.\n\n", NamedTextColor.WHITE),
                            Component.text("보상", NamedTextColor.GRAY),
                            Component.text(" >> ", NamedTextColor.DARK_GRAY),
                            prefix,
                            Component.text(" 칭호", NamedTextColor.WHITE)
                    ).build(),
                    null,
                    AdvancementDisplay.Frame.TASK,
                    2,
                    1,
                    true,
                    true,
                    false
            ).parent(Noah.getInstance()
                    .getAdvancementManager()
                    .getConstant(AdvancementKeys.MVP_KILLS_5)
                    .getAdvancement()
            ).useCount(15);
        };
    }

    @Override
    public AdvancementAward getAward() {
        return (player, constant) -> {
            User user = User.get(player);
            user.getDataContainer().getOrLoadAsync(PrefixData.class).thenApply(prefixData -> {
                prefixData.addPrefix(PREFIX_KEY);
                Prefix.sendPrefixGetAlart(player, PREFIX_KEY);
                return prefixData;
            });
        };
    }
}
