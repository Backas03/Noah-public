package org.caramel.backas.noah.game.deathmatch;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import kr.lostwar.fmj.api.FMJ;
import kr.lostwar.fmj.api.events.WeaponBreakGlassEvent;
import kr.lostwar.fmj.api.events.WeaponBurnBlockEvent;
import kr.lostwar.fmj.api.events.WeaponDamageEntityEvent;
import kr.lostwar.fmj.api.events.WeaponExplodeBlockEvent;
import kr.lostwar.fmj.api.player.FMJPlayer;
import kr.lostwar.fmj.api.weapon.Weapon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.api.event.user.afk.AFKEndEvent;
import org.caramel.backas.noah.api.event.user.afk.AFKStartEvent;
import org.caramel.backas.noah.api.user.User;
import org.caramel.backas.noah.resource.DeathMatchResource;
import org.caramel.backas.noah.util.GameUtil;
import org.jetbrains.annotations.NotNull;

public class DeathMatchListener implements Listener {

    @EventHandler
    public void onFallDamage(EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onRegainHealth(EntityRegainHealthEvent e) {
        if (e.getRegainReason() != EntityRegainHealthEvent.RegainReason.MAGIC_REGEN) e.setCancelled(true);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        User user = User.get(e.getPlayer());
        if (GameUtil.gameEquals(user, DeathMatchGame.class)) {
            DeathMatchChannel<?> channel = (DeathMatchChannel<?>) user.getGameData().getChannel();
            channel.onDeathEvent(null, e.getPlayer());
        }
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent e) {
        if (e.getPlayer() instanceof Player player) {
            User user = User.get(player);
            if (GameUtil.gameEquals(user, DeathMatchGame.class)) {
                DeathMatchChannel<?> channel = (DeathMatchChannel<?>) user.getGameData().getChannel();
                Inventory inventory = channel.getGunInventory();
                if (e.getInventory().equals(inventory)) {
                    Bukkit.getScheduler().runTaskLater(Noah.getInstance(), () -> {
                        DeathMatchParticipant p = channel.getParticipants().get(user.getUniqueId());
                        if (p != null && (p.isRespawning() || p.getNextWeapon() == null)) {
                            player.openInventory(inventory);
                        }
                    }, 1L);
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player attacker && e.getEntity() instanceof Player victim) {
            User pa = User.get(attacker);
            User pv = User.get(victim);
            if (GameUtil.gameEquals(pa, DeathMatchGame.class) && GameUtil.gameEquals(pa, pv, true)) {
                DeathMatchChannel<?> channel = (DeathMatchChannel<?>) pa.getGameData().getChannel();
                DeathMatchParticipant p1 = channel.getParticipants().get(pa.getUniqueId());
                DeathMatchParticipant p2 = channel.getParticipants().get(pv.getUniqueId());
                if (p1 == null || p2 == null) {
                    e.setCancelled(true);
                    return;
                }
                if (p2.isGodMode()) {
                    e.setCancelled(true);
                    return;
                }
                if (p1.getTeam() == p2.getTeam()) {
                    e.setCancelled(true);
                    return;
                }
                channel.onDamageEvent(attacker, victim, e.getDamage());
                if (e.getDamage() > victim.getHealth()) {
                    e.setCancelled(true);
                    channel.onDeathEvent(attacker, victim);
                }
            }
        }
    }

    @EventHandler
    public void onFKey(PlayerSwapHandItemsEvent e) {
        User user = User.get(e.getPlayer());
        if (GameUtil.gameEquals(user, DeathMatchGame.class)) {
            e.setCancelled(true);
            DeathMatchChannel<?> channel = (DeathMatchChannel<?>) user.getGameData().getChannel();
            if (channel.getParticipants() == null) return;
            DeathMatchParticipant participant = channel.getParticipants().get(user.getUniqueId());
            if (participant == null) return;
            if (participant.isRespawning()) {
                e.getPlayer().openInventory(channel.getGunInventory());
            }
        }
    }

    @EventHandler
    public void onJump(PlayerJumpEvent e) {
        User user = User.get(e.getPlayer());
        if (GameUtil.gameEquals(user, DeathMatchGame.class)) {
            DeathMatchChannel<?> channel = (DeathMatchChannel<?>) user.getGameData().getChannel();
            if (channel.getParticipants() == null) return;
            DeathMatchParticipant participant = channel.getParticipants().get(user.getUniqueId());
            if (participant == null) return;
            if (participant.isRespawning()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player player) {
            User user = User.get(player);
            if (GameUtil.gameEquals(user, DeathMatchGame.class)) {
                e.setCancelled(true);
                DeathMatchGame game = (DeathMatchGame) user.getGameData().getGame();
                DeathMatchChannel<?> channel = (DeathMatchChannel<?>) user.getGameData().getChannel();
                if (e.getRawSlot() < game.getConfig().getGunInventoryRows().get() * 9) {
                    if (e.getView().getTopInventory().equals(channel.getGunInventory())) {
                        ItemStack i = e.getCurrentItem();
                        if (i != null && i.getType() != Material.AIR) {
                            Weapon weapon = FMJ.findWeapon(i);
                            if (weapon != null) {
                                DeathMatchParticipant tdmp = channel.getParticipants().get(user.getUniqueId());
                                if (tdmp.getNextWeapon() == null) {
                                    tdmp.setNextWeapon(weapon);
                                    channel.giveWeapon(player);
                                    player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                                } else {
                                    tdmp.setNextWeapon(weapon);
                                    player.sendMessage("다음 무기 : " + weapon.getName());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent e) {
        User user = User.get(e.getPlayer());
        if (user.isInGame()) e.setCancelled(true);
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        ItemStack i = e.getPlayer().getInventory().getItemInMainHand();
        if (!e.getAction().isRightClick()) return;
        if (i.getType() == Material.AIR) return;
        User user = User.get(e.getPlayer());
        if (GameUtil.gameEquals(user, DeathMatchGame.class)) {
            e.setCancelled(true);
            ItemStack healKit = DeathMatchResource.getHealKitItem();
            if (i.isSimilar(healKit)) {
                e.getPlayer().getInventory().remove(healKit);
                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 4));
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            }
        }
    }

    @EventHandler
    public void onWeaponDamage(WeaponDamageEntityEvent e) {
        if (e.getVictim() instanceof Player victim) {
            User ua = User.get(e.getPlayer());
            User uv = User.get(victim);
            if (GameUtil.gameEquals(ua, DeathMatchGame.class) && GameUtil.gameEquals(ua, uv, true)) {
                DeathMatchChannel<?> channel = (DeathMatchChannel<?>) ua.getGameData().getChannel();
                DeathMatchParticipant p1 = channel.getParticipants().get(ua.getUniqueId());
                DeathMatchParticipant p2 = channel.getParticipants().get(uv.getUniqueId());
                if (p2.isGodMode()) {
                    e.setCancelled(true);
                    return;
                }
                if (p1.getTeam() == p2.getTeam()) {
                    e.setCancelled(true);
                    return;
                }
                channel.onDamageEvent(e.getPlayer(), victim, e.getDamage());
                if (e.getDamage() > victim.getHealth()) {
                    e.setCancelled(true);
                    channel.onDeathEvent(e.getPlayer(), victim);
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        User user = User.get(e.getPlayer());
        if (user.isLoad() && user.isInGame() && GameUtil.gameEquals(user, DeathMatchGame.class)) {
            DeathMatchChannel<?> channel = (DeathMatchChannel<?>) user.getGameData().getChannel();
            DeathMatchParticipant p = channel.getParticipants().get(user.getUniqueId());
            if (p == null) return;
            if (channel.getGameBar() != null) {
                e.getPlayer().showBossBar(channel.getGameBar());
            }
            if (channel.getScoreboard() != null) {
                e.getPlayer().setScoreboard(channel.getScoreboard());
            }
            if (channel.getQuitLocation() != null) {
                QuitData data = channel.getQuitLocation().get(user.getUniqueId());
                if (data != null) {
                    e.getPlayer().teleport(data.getLocation());
                    data.setTime(data.getTime() + data.getQuitTime() - channel.getTime());
                }
                if (Noah.getLobby().getRegion().isInside(user)) {
                    e.getPlayer().teleport(channel.getGameMap().getSpawn(p.getTeam()));
                }
                if (p.getNextWeapon() == null) {
                    e.getPlayer().openInventory(channel.getGunInventory());
                    return;
                }
                /* give weapon */
                FMJPlayer fmjPlayer = FMJ.getFMJPlayer(e.getPlayer());
                Weapon mainWeapon = p.getNextWeapon();
                if (mainWeapon != null) {
                    e.getPlayer().getInventory().setItem(0, mainWeapon.getItemStack());
                }
                Weapon subWeapon = FMJ.findWeapon(channel.getGame().getConfig().getSubWeaponKey().get());
                if (subWeapon != null) e.getPlayer().getInventory().setItem(1, subWeapon.getItemStack());
                fmjPlayer.unsafeStatusReload(); // DEPRECATED
            }
        }
    }

    @EventHandler
    public void onAFKStart(AFKStartEvent e) {
        User user = e.getUser();
        if (user.isLoad() && user.isInGame() && GameUtil.gameEquals(user, DeathMatchGame.class)) {
            user.sendMessage(Component.text()
                    .content("경고! 움직이지 않으면 탈주 패널티를 받을 수 있습니다.")
                    .color(NamedTextColor.RED)
                    .build()
            );
        }
    }

    @EventHandler
    public void onAFKEnd(AFKEndEvent e) {
        User user = e.getUser();
        if (user.isLoad() && user.isInGame() && GameUtil.gameEquals(user, DeathMatchGame.class)) {
            DeathMatchChannel<?> channel = (DeathMatchChannel<?>) user.getGameData().getChannel();
            QuitData data = channel.getQuitLocation().get(user.getUniqueId());
            if (data == null) {
                data = new QuitData(null, e.getSecond(), channel.getTime());
            } else {
                data.setTime(data.getTime() + e.getSecond());
            }
            channel.getQuitLocation().put(user.getUniqueId(), data);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        User user = User.get(e.getPlayer());
        if (user.isLoad() && user.isInGame() && GameUtil.gameEquals(user, DeathMatchGame.class)) {
            DeathMatchChannel<?> channel = (DeathMatchChannel<?>) user.getGameData().getChannel();
            QuitData data = channel.getQuitLocation().get(e.getPlayer().getUniqueId());
            if (data != null) {
                data.setLocation(e.getPlayer().getLocation());
                data.setQuitTime(channel.getTime());
            } else {
                data = new QuitData(e.getPlayer().getLocation(), 0, channel.getTime());
            }
            channel.getQuitLocation().put(e.getPlayer().getUniqueId(), data);
        }
    }


    @EventHandler
    public void onWeaponBreakGlass(WeaponBreakGlassEvent e) {
        this.checkInGame(User.get(e.getPlayer()), e);
    }

    @EventHandler
    public void onWeaponExplodeBlock(WeaponExplodeBlockEvent e) {
        this.checkInGame(User.get(e.getPlayer()), e);
    }

    @EventHandler
    public void onWeaponBurnBlock(WeaponBurnBlockEvent e) {
        this.checkInGame(User.get(e.getShooter()), e);
    }

    /**
     * 플레이어가 게임에 참여 중인지 확인 후, 아니라면 이벤트를 캔슬합니다.
     *
     * @param user 유저
     * @param e 캔슬이 가능한 이벤트
     */
    void checkInGame(@NotNull final User user, @NotNull final Cancellable e) {
        if (!GameUtil.gameEquals(user, DeathMatchGame.class)) e.setCancelled(true);
    }
}
