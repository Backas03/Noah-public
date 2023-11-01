package org.caramel.backas.noah.advancement.impl.count;

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

import java.util.function.Consumer;

public class AdvancementCount10 implements AdvancementData {

    public static final String PREFIX_KEY = "전문가";

    @Override
    public String getKey() {
        return AdvancementKeys.ADVANCEMENT_ARCHIVE_COUNT_10;
    }

    @Override
    public Consumer<AdvancementBuilder> getBuilder() {
        return builder -> {
            ItemStack icon = new ItemStack(Material.EMERALD);
            Component prefix = Prefix.getComponent(PREFIX_KEY);

            builder.display(
                    icon,
                    Component.text(PREFIX_KEY, prefix.color()),
                    Component.text().append(
                            Component.text("도전과제를", NamedTextColor.GOLD),
                            Component.text(" 10개", NamedTextColor.GREEN),
                            Component.text(" 완료하세요.\n\n", NamedTextColor.WHITE),
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
                    .getConstant(AdvancementKeys.ADVANCEMENT_ARCHIVE_COUNT_5)
                    .getAdvancement()
            ).useCount(10);
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
