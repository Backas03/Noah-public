package org.caramel.backas.noah.game.tdm.inventory;

import kr.lostwar.fmj.api.FMJ;
import kr.lostwar.fmj.api.weapon.Weapon;
import moe.caramel.acacia.api.inventory.page.model.ArrayedPageInventory;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.game.tdm.TDMGameOption;
import org.caramel.backas.noah.game.tdm.TDMParticipant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TDMGunSelectInventory extends ArrayedPageInventory {

    public static void open(TDMParticipant participant) {
        int inventorySize = (int) Math.min(
                TDMGameOption.GUN_INVENTORY_ROWS * 9,
                Math.ceil((double) TDMGameOption.WEAPON_KEYS.length / 9)
        ) * 9;
        List<TDMGunSelectContent> contents = new ArrayList<>();
        for (String key : TDMGameOption.WEAPON_KEYS) {
            Weapon weapon = FMJ.findWeapon(key);
            if (weapon != null) {
                contents.add(new TDMGunSelectContent(participant, weapon));
            }
        }
        TDMGunSelectInventory inventory = new TDMGunSelectInventory(participant, inventorySize, contents);
        participant.getUser().getPlayer().ifPresent(player -> player.openInventory(inventory.inventory));
    }

    private final TDMParticipant participant;

    private TDMGunSelectInventory(TDMParticipant participant, int inventorySize, @NotNull List<TDMGunSelectContent> contents) {
        super(inventorySize, inventorySize, contents);
        this.participant = participant;
    }

    @Override
    protected @Nullable Component createTitle() {
        if (participant.nextWeapon == null) return Component.text("클릭하여 무기를 선택합니다.");
        else return Component.text("다음 무기: " + participant.nextWeapon.getName());
    }

    @Override
    protected void onClick(@NotNull InventoryClickEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void process(@NotNull InventoryCloseEvent event) {
        if (event.getReason() != InventoryCloseEvent.Reason.PLUGIN && (participant.isRespawning() || participant.nextWeapon == null)) {
            Bukkit.getScheduler().runTaskLater(Noah.getInstance(), () -> event.getPlayer().openInventory(this.inventory), 1L);
        }
    }
}
