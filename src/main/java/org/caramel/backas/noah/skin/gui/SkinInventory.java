package org.caramel.backas.noah.skin.gui;

import kr.lostwar.fmj.api.FMJ;
import kr.lostwar.fmj.api.weapon.Weapon;
import moe.caramel.acacia.api.inventory.page.model.ArrayedPageInventory;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.game.tdm.TDMGameOption;
import org.caramel.backas.noah.skin.gui.content.SkinContent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SkinInventory extends ArrayedPageInventory {

    public static void open(Player player) {
        List<SkinContent> contents = new ArrayList<>();
        for (String key : TDMGameOption.WEAPON_KEYS) {
            Weapon weapon = FMJ.findWeapon(key);
            if (weapon != null) {
                contents.add(new SkinContent(weapon));
            }
        }
        Bukkit.getScheduler().runTask(Noah.getInstance(), () -> player.openInventory(new SkinInventory(contents).inventory));
    }

    private SkinInventory(@NotNull List<? extends @NotNull ItemStack> contents) {
        super(9,9, contents);
    }

    @Override
    protected @Nullable Component createTitle() {
        return Component.text("수정할 스킨의 타입을 선택하세요");
    }
}
