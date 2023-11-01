package org.caramel.backas.noah.skin.gui;

import moe.caramel.acacia.api.inventory.page.model.ArrayedPageInventory;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.skin.Skin;
import org.caramel.backas.noah.skin.SkinData;
import org.caramel.backas.noah.skin.SkinEffect;
import org.caramel.backas.noah.skin.gui.content.SkinEffectContent;
import org.caramel.backas.noah.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SkinEffectSelectInventory extends ArrayedPageInventory {

    public static void open(Player player, Skin skin) {
        List<SkinEffectContent> contents = new ArrayList<>();
        User.get(player).getDataContainer().getOrLoadAsync(SkinData.class).thenApply(skinData -> {
            for (SkinEffect effect : skin.getEffects()) {

                ItemStack current = !skinData.hasEffect(skin, effect.getId()) ? effect.getPurchaseIcon() :
                        skinData.isEffectEnabled(skin, effect.getId()) ?
                                effect.getEnabledIcon() :
                                effect.getDisabledIcon();

                contents.add(new SkinEffectContent(effect, current));
            }
            int inventorySize = (int) Math.max(1, Math.ceil((double) contents.size() / 9)) * 9;
            Bukkit.getScheduler().runTask(Noah.getInstance(), () -> player.openInventory(new SkinEffectSelectInventory(inventorySize, contents).inventory));
            return skinData;
        });
    }

    protected SkinEffectSelectInventory(int inventorySize, @NotNull List<? extends @NotNull ItemStack> contents) {
        super(inventorySize, inventorySize, contents);
    }

    @Override
    protected @Nullable Component createTitle() {
        return Component.text("스킨 이펙트를 설정합니다");
    }
}
