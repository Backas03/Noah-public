package org.caramel.backas.noah.advancement.impl.kills;

import io.papermc.paper.advancement.AdvancementDisplay;
import moe.caramel.daydream.advancement.AdvancementBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.caramel.backas.noah.advancement.AdvancementAward;
import org.caramel.backas.noah.advancement.AdvancementData;
import org.caramel.backas.noah.advancement.AdvancementKeys;
import org.caramel.backas.noah.util.ItemStackUtil;

import java.util.function.Consumer;

public class AdvancementKillsRoot implements AdvancementData {

    @Override
    public String getKey() {
        return AdvancementKeys.ROOT_KILLS;
    }

    @Override
    public Consumer<AdvancementBuilder> getBuilder() {
        return builder -> {
            ItemStack icon = ItemStackUtil.get(new ItemStack(Material.STONE_PICKAXE), 100000);
            builder.visibleRoot()
                    .display(
                            icon,
                            Component.text("킬 수 달성"),
                            null,
                            NamespacedKey.minecraft("textures/block/sand.png"),
                            AdvancementDisplay.Frame.TASK,
                            true,
                            true,
                            false
                    );
        };
    }

    @Override
    public AdvancementAward getAward() {
        return null;
    }
}
