package org.caramel.backas.noah.advancement.impl.win;

import io.papermc.paper.advancement.AdvancementDisplay;
import moe.caramel.daydream.advancement.AdvancementBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.caramel.backas.noah.advancement.AdvancementAward;
import org.caramel.backas.noah.advancement.AdvancementData;
import org.caramel.backas.noah.advancement.AdvancementKeys;

import java.util.function.Consumer;

public class AdvancementWinRoot implements AdvancementData {

    @Override
    public String getKey() {
        return AdvancementKeys.ROOT_WIN;
    }

    @Override
    public Consumer<AdvancementBuilder> getBuilder() {
        return builder -> {
            ItemStack icon = new ItemStack(Material.FIREWORK_ROCKET);
            builder.visibleRoot()
                    .display(
                            icon,
                            Component.text("승리 하기"),
                            null,
                            NamespacedKey.minecraft("textures/block/moss_block.png"),
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
