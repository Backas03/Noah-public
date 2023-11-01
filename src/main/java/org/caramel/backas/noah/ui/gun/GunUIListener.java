package org.caramel.backas.noah.ui.gun;

import kr.lostwar.fmj.api.FMJ;
import kr.lostwar.fmj.api.events.WeaponChangeEvent;
import kr.lostwar.fmj.api.events.WeaponChangeSelectorEvent;
import kr.lostwar.fmj.api.events.WeaponReloadEndEvent;
import kr.lostwar.fmj.api.events.WeaponReloadEvent;
import kr.lostwar.fmj.api.events.WeaponShootEvent;
import kr.lostwar.fmj.api.events.WeaponUpdateStatusEvent;
import kr.lostwar.fmj.api.player.FMJPlayer;
import kr.lostwar.fmj.api.weapon.Weapon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.caramel.backas.noah.prefix.Prefix;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GunUIListener implements Listener {

    private final Set<UUID> RELOADING = new HashSet<>();

    @EventHandler
    public void onModeChange(WeaponChangeSelectorEvent e) {
        sendAmmo(e.getPlayer(), e.getWeapon());
    }

    @EventHandler
    public void onShoot(WeaponShootEvent e) {
        sendAmmo(e.getPlayer(), e.getWeapon());
    }

    @EventHandler
    public void onReloadEnd(WeaponReloadEndEvent event) {
        RELOADING.remove(event.getPlayer().getUniqueId());
        sendAmmo(event.getPlayer(), event.getWeapon());
    }

    @EventHandler
    public void onReload(WeaponReloadEvent e) {
        RELOADING.add(e.getPlayer().getUniqueId());
        sendAmmo(e.getPlayer(), e.getWeapon());
    }

    @EventHandler
    public void onWeaponStatusUpdate(WeaponUpdateStatusEvent e) {
        sendAmmo(e.getPlayer(), e.getWeapon());
    }

    @EventHandler
    public void onWeaponHeld(WeaponChangeEvent event) {
        if (event.getAfterWeapon() == null) {
            event.getPlayer().setLevel(0);
            event.getPlayer().setExp(0);
        }
        RELOADING.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onWeaponModeChange(WeaponChangeSelectorEvent event) {
        Weapon weapon = event.getWeapon();
        Player player = event.getPlayer();
        player.sendMessage(Component.text().append(
                Prefix.INFO_WITH_SPACE,
                Component.text(weapon.getName() + " 사격 모드: "),
                Component.text(event.getAfter().getDefaultName(), NamedTextColor.YELLOW)
        ));
    }

    private void sendAmmo(Player player, Weapon weapon) {
        if (weapon == null) return;
        FMJPlayer fmjPlayer = FMJ.getFMJPlayer(player);
        if (!fmjPlayer.isHeldingWeapon()) {
            return;
        }
        int ammo = RELOADING.contains(player.getUniqueId()) ? 0 : fmjPlayer.getAmmo();
        int maxAmmo = weapon.getReload().getAmount();
        float progress = (float) ammo / maxAmmo;
        player.setLevel(ammo); // left ammo
        player.setExp(progress); // progress
    }
}
