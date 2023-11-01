package org.caramel.backas.noah.advancement.impl.win;

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

public class AdvancementWin50 implements AdvancementData {

    public static final String PREFIX_KEY = "챔피언";

    @Override
    public String getKey() {
        return AdvancementKeys.WIN_50;
    }

    @Override
    public Consumer<AdvancementBuilder> getBuilder() {
        return builder -> {
            ItemStack icon = new ItemStack(Material.SUNFLOWER);
            Component prefix = Prefix.getComponent(PREFIX_KEY);

            builder.display(
                    icon,
                    Component.text(PREFIX_KEY, prefix.color()),
                    Component.text().append(
                            Component.text("5킬 이상으로 50번 승리", NamedTextColor.RED),
                            Component.text("하세요.\n\n", NamedTextColor.WHITE),
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
                    .getConstant(AdvancementKeys.WIN_20)
                    .getAdvancement()
            ).useCount(50);
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
