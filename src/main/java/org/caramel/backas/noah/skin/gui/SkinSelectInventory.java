package org.caramel.backas.noah.skin.gui;

import kr.lostwar.fmj.api.weapon.Weapon;
import moe.caramel.acacia.api.inventory.page.Content;
import moe.caramel.acacia.api.inventory.page.model.ArrayedPageInventory;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.skin.Skin;
import org.caramel.backas.noah.skin.SkinData;
import org.caramel.backas.noah.skin.gui.content.SkinSelectContent;
import org.caramel.backas.noah.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SkinSelectInventory extends ArrayedPageInventory {

    public static void open(Weapon origin, Player player) {
        User.get(player).getDataContainer().getOrLoadAsync(SkinData.class).thenApply(skinData -> {
            List<SkinSelectContent> contents = new ArrayList<>();
            for (Skin skin : skinData.getSkins()) {
                skin.getWeapon().ifPresent(weapon -> {
                    if (origin.equals(weapon)) {
                        contents.add(new SkinSelectContent(weapon));
                    }
                });
            }
            Bukkit.getScheduler().runTask(Noah.getInstance(), () -> player.openInventory(new SkinSelectInventory(contents).inventory));
            return skinData;
        });
    }

    private SkinSelectInventory(@NotNull List<? extends @NotNull ItemStack> contents) {
        super(54, 45, contents);
    }

    @Override
    protected void preInitializeInventory() {
        this.setItem(52, new Content(new ItemStack(Material.STICK), ((event, inv, clicked) -> inv.previousPage())));
        this.setItem(53, new Content(new ItemStack(Material.BLAZE_ROD), (event, inv, clicked) -> inv.nextPage()));
    }

    @Override
    protected void onClick(@NotNull InventoryClickEvent event) {
        event.setCancelled(true);
    }

    @Override
    protected @Nullable Component createTitle() {
        return Component.text("쉬프트 + 클릭: 스킨 착용 | 클릭: 이펙트 설정 [" + getCurrentPage() + "/" + getMaxPage() + "]");
    }
}
