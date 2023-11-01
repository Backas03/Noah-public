package org.caramel.backas.noah.game.tdm.inventory;

import kr.lostwar.fmj.api.weapon.Weapon;
import moe.caramel.acacia.api.inventory.page.Content;
import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.caramel.backas.noah.game.tdm.GameTDM;
import org.caramel.backas.noah.game.tdm.TDMParticipant;
import org.jetbrains.annotations.NotNull;

public class TDMGunSelectContent extends Content {

    public TDMGunSelectContent(TDMParticipant participant, @NotNull Weapon weapon) {
        super(weapon.getItemStack(), (event, inventory, clicked) -> {
            if (participant.nextWeapon == null) { // 처음 총기를 선택하는 창일 때
                participant.nextWeapon = weapon;
                participant.getUser().getPlayer().ifPresent(player -> GameTDM.giveWeapon(player, participant));
                event.getWhoClicked().closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                return;
            }
            participant.nextWeapon = weapon;
            event.getView().sendTitleUpdate(Component.text("다음 무기: " + weapon.getName()));
        });
    }
}
