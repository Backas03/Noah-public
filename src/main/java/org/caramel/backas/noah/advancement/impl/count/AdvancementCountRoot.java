package org.caramel.backas.noah.advancement.impl.count;

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

public class AdvancementCountRoot implements AdvancementData {
    @Override
    public String getKey() {
        return AdvancementKeys.ROOT_ADVANCEMENT_ARCHIVE;
    }

    @Override
    public Consumer<AdvancementBuilder> getBuilder() {
        return builder -> {
            ItemStack icon = new ItemStack(Material.EMERALD);
            builder.visibleRoot()
                    .display(
                            icon,
                            Component.text("도전과제 달성"),
                            null,
                            NamespacedKey.minecraft("textures/block/green_concrete_powder.png"),
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
