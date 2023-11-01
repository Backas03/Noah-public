package org.caramel.backas.noah.listener;

import kr.lostwar.fmj.api.FMJ;
import kr.lostwar.fmj.api.events.*;
import kr.lostwar.fmj.api.player.FMJPlayer;
import kr.lostwar.fmj.api.weapon.Weapon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.api.game.matching.MatchingPool;
import org.caramel.backas.noah.api.user.User;

public class GameListener implements Listener {

    @EventHandler
    public void onFKey(PlayerSwapHandItemsEvent e) {
        User user = User.get(e.getPlayer());
        if (user.getGameData() != null && user.getGameData().isEnqueue() && !e.getPlayer().isSneaking()) {
            MatchingPool.dequeue(user.getParty());
        }
        if (Noah.getLobby().getRegion().isInside(user)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamageEvent(EntityDamageEvent e) {
        if (e.getCause() != EntityDamageEvent.DamageCause.CUSTOM) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if(event.toWeatherState()) event.setCancelled(true);
    }

    @EventHandler
    public void onThunderChange(ThunderChangeEvent event) {
        if(event.toThunderState()) event.setCancelled(true);
    }

    @EventHandler
    public void onShoot(WeaponShootEvent e) {
        sendAmmo(e.getPlayer(), e.getWeapon());
    }

    @EventHandler
    public void onReload(WeaponReloadEvent e) {
        sendAmmo(e.getPlayer(), e.getWeapon());
    }

    @EventHandler
    public void onReloadEnd(WeaponReloadEndEvent e) {
        sendAmmo(e.getPlayer(), e.getWeapon());
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e) {
        if (!Noah.getLobby().getRegion().isInside(e.getPlayer()) || !e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getWhoClicked().isOp()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (!e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (!e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent e) {
        if (!e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onWeaponStatusUpdate(WeaponUpdateStatusEvent e) {
        sendAmmo(e.getPlayer(), e.getWeapon());
    }

    @EventHandler
    public void onModeChange(WeaponChangeSelectorEvent e) {
        sendAmmo(e.getPlayer(), e.getWeapon());
    }

    private void sendAmmo(Player player, Weapon weapon) {
        if (weapon == null) return;
        FMJPlayer fmjPlayer = FMJ.getFMJPlayer(player);
        if (!fmjPlayer.isHeldingWeapon()) return;
        int maxAmmo = weapon.getReload().getAmount();
        String mode = fmjPlayer.getSelector().getDefaultName();
        player.sendActionBar(fmjPlayer.getAmmo() + "/" + maxAmmo + "    " + mode);
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent e) {
        e.setCancelled(true);
    }
}
