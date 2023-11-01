package org.caramel.backas.noah.advancement.impl.mvp.assist;

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

public class AdvancementAssistMVPRoot implements AdvancementData {

    @Override
    public String getKey() {
        return AdvancementKeys.ROOT_ASSIST_MVP;
    }

    @Override
    public Consumer<AdvancementBuilder> getBuilder() {
        return builder -> {
            ItemStack icon = ItemStackUtil.get(new ItemStack(Material.IRON_PICKAXE), 50001);
            builder.visibleRoot()
                    .display(
                            icon,
                            Component.text("Assist MVP 달성"),
                            null,
                            NamespacedKey.minecraft("textures/block/red_concrete_powder.png"),
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
