package org.caramel.backas.noah.skin.gui.content;

import kr.lostwar.fmj.api.weapon.Weapon;
import moe.caramel.acacia.api.inventory.page.Content;
import org.bukkit.entity.Player;
import org.caramel.backas.noah.skin.gui.SkinSelectInventory;

public class SkinContent extends Content {

    public SkinContent(Weapon weapon) {
        super(weapon.getItemStack());
        this.setEvent((event, inventory, clicked) -> {
            event.setCancelled(true);
            if (event.getWhoClicked() instanceof Player player) {
                SkinSelectInventory.open(weapon, player);
            }
        });
    }
}
