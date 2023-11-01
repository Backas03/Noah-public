package org.caramel.backas.noah.advancement.impl.mvp.kill;

import io.papermc.paper.advancement.AdvancementDisplay;
import moe.caramel.daydream.advancement.AdvancementBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.caramel.backas.noah.advancement.AdvancementAward;
import org.caramel.backas.noah.advancement.AdvancementData;
import org.caramel.backas.noah.advancement.AdvancementKeys;

import java.util.function.Consumer;

public class AdvancementKillMVPRoot implements AdvancementData {

    @Override
    public String getKey() {
        return AdvancementKeys.ROOT_KILL_MVP;
    }

    @Override
    public Consumer<AdvancementBuilder> getBuilder() {
        return builder -> {
            ItemStack icon = new ItemStack(Material.LARGE_AMETHYST_BUD);
            builder.visibleRoot()
                    .display(
                            icon,
                            Component.text("Kill MVP 달성"),
                            null,
                            NamespacedKey.minecraft("textures/block/stripped_oak_log.png"),
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
