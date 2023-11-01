package org.caramel.backas.noah.skin.gui.content;

import moe.caramel.acacia.api.inventory.page.Content;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.caramel.backas.noah.skin.SkinData;
import org.caramel.backas.noah.skin.SkinEffect;
import org.caramel.backas.noah.skin.SkinException;
import org.caramel.backas.noah.user.User;

public class SkinEffectContent extends Content {

    public SkinEffectContent(SkinEffect effect, ItemStack current) {
        super(current);
        this.setEvent((event, inventory, clicked) -> {
            event.setCancelled(true);
            if (event.getWhoClicked() instanceof Player player) {
                User user = User.get(player);
                user.getDataContainer().getOrLoadAsync(SkinData.class).thenApply(skinData -> {
                    Boolean enabled = skinData.isEffectEnabled(effect.getSkin(), effect.getId());
                    if (enabled != null) {
                        try {
                            enabled = !enabled;
                            skinData.setEffectEnable(effect.getSkin(), effect.getId(), enabled);
                            inventory.setItem(event.getRawSlot(), new SkinEffectContent(effect, enabled ? effect.getEnabledIcon() : effect.getDisabledIcon()));
                        } catch (SkinException e) {
                            player.sendMessage(e.getComponent());
                        }
                    }
                    return skinData;
                });
            }
        });
    }
}
