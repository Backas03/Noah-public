package org.caramel.backas.noah.advancement.impl.mvp.assist;

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

public class AdvancementAssistMVP5 implements AdvancementData {

    public static final String PREFIX_KEY = "도우미";

    @Override
    public String getKey() {
        return AdvancementKeys.MVP_ASSIST_5;
    }

    @Override
    public Consumer<AdvancementBuilder> getBuilder() {
        return builder -> {
            // HealKit icon
            ItemStack icon = ItemStackUtil.get(new ItemStack(Material.IRON_PICKAXE), 50001);
            Component prefix = Prefix.getComponent(PREFIX_KEY);

            builder.display(
                    icon,
                    Component.text(PREFIX_KEY, prefix.color()),
                    Component.text().append(
                            Component.text("게임 종료시인원", NamedTextColor.WHITE),
                            Component.text(" 6명 이상인 게임", NamedTextColor.YELLOW),
                            Component.text("에서", NamedTextColor.WHITE),
                            Component.text(" Assist MVP 5번", NamedTextColor.GOLD),
                            Component.text("을 달성하세요.\n\n", NamedTextColor.WHITE),
                            Component.text("보상", NamedTextColor.GRAY),
                            Component.text(" >> ", NamedTextColor.DARK_GRAY),
                            prefix,
                            Component.text(" 칭호", NamedTextColor.WHITE)
                    ).build(),
                    null,
                    AdvancementDisplay.Frame.TASK,
                    1,
                    1,
                    true,
                    true,
                    false
            ).parent(Noah.getInstance()
                    .getAdvancementManager()
                    .getConstant(AdvancementKeys.ROOT_ASSIST_MVP)
                    .getAdvancement()
            ).useCount(5);
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
