package org.caramel.backas.noah.advancement.impl.killstreak;

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

public class AdvancementKillStreakRoot implements AdvancementData {

    @Override
    public String getKey() {
        return AdvancementKeys.ROOT_KILL_STREAK;
    }

    @Override
    public Consumer<AdvancementBuilder> getBuilder() {
        return builder -> {
            ItemStack icon = new ItemStack(Material.IRON_SWORD);
            builder.visibleRoot()
                    .display(
                            icon,
                            Component.text("연속 킬 처치"),
                            null,
                            NamespacedKey.minecraft("textures/block/white_concrete_powder.png"),
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
