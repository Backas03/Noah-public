package org.caramel.backas.noah.skin.gui.content;

import kr.lostwar.fmj.api.weapon.Weapon;
import moe.caramel.acacia.api.inventory.page.Content;
import org.bukkit.entity.Player;
import org.caramel.backas.noah.skin.Skin;
import org.caramel.backas.noah.skin.SkinData;
import org.caramel.backas.noah.skin.SkinException;
import org.caramel.backas.noah.skin.gui.SkinEffectSelectInventory;
import org.caramel.backas.noah.user.User;

public class SkinSelectContent extends Content {

    public SkinSelectContent(Weapon model) {
        super(model.getItemStack());
        this.setEvent((event, inventory, clicked) -> {
            event.setCancelled(true);
            if (event.getWhoClicked() instanceof Player player) {
                Skin skin = Skin.find(model);
                if (skin != null) {
                    if (event.getClick().isShiftClick()) {
                        player.closeInventory();
                        User.get(player).getDataContainer().getOrLoadAsync(SkinData.class).thenApply(skinData -> {
                            try {
                                skinData.setCurrentSkin(skin);
                            } catch (SkinException e) {
                                player.sendMessage(e.getComponent());
                            }
                            return skinData;
                        });
                        return;
                    }
                    SkinEffectSelectInventory.open(player, skin);
                }
            }
        });
    }
}
