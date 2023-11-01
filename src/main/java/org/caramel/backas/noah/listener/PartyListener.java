package org.caramel.backas.noah.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.api.event.game.GameEnqueueEvent;
import org.caramel.backas.noah.api.event.party.PartyInviteEvent;
import org.caramel.backas.noah.api.event.party.PartyLeaveEvent;
import org.caramel.backas.noah.api.game.AbstractGame;
import org.caramel.backas.noah.api.game.GameManager;
import org.caramel.backas.noah.api.game.matching.MatchingPool;
import org.caramel.backas.noah.api.party.Party;
import org.caramel.backas.noah.api.party.PartyManager;
import org.caramel.backas.noah.api.user.User;
import org.caramel.backas.noah.deprecated.ResourceIds;
import org.caramel.backas.noah.game.personaldeathmatch.PDMGame;
import org.caramel.backas.noah.game.teamdeathmatch.TDMGame;
import org.caramel.backas.noah.resource.PartyResource;
import org.caramel.backas.noah.util.ColorString;
import org.caramel.backas.noah.util.HeadItemUtil;
import org.caramel.backas.noah.util.ItemStackUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PartyListener implements Listener {

    @EventHandler
    public void onEnqueue(GameEnqueueEvent e) {
        PartyManager.removeAllUserInvites(e.getParty());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        PartyManager.leaveParty(User.get(e.getPlayer()));
    }

    @EventHandler
    public void onPartyLeave(PartyLeaveEvent e) {
        for (User user : e.getParty().getAllMembers()) {
            Player player = user.toPlayer();
            if (player != null && player.getOpenInventory().getTitle().equals("파티 정보")) {
                openPartyInfoMenu(user);
            }
        }
    }

    @EventHandler
    public void onPartyInvite(PartyInviteEvent e) {
        Player player = e.getInvitor().toPlayer();
        if (player != null && player.getOpenInventory().getTitle().equals("파티 초대 목록")) {
                openPartyInviteMenu(e.getInvitor());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractAtEntityEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (e.getRightClicked() instanceof Player target) {
            Player player = e.getPlayer();
            User user = User.get(player);
            User targetUser = User.get(target);
            if (player.isSneaking()) {
                if (Noah.getLobby().getRegion().isInside(user) && Noah.getLobby().getRegion().isInside(targetUser)) {
                    if (!user.hasParty()) {
                        PartyManager.createNewParty(user);
                    }
                    PartyManager.invite(user, targetUser);
                }
            }
        }
    }

    private void playSound(Player player) {
        player.playSound(player.getLocation(), "tdm.click", 1, 1);
    }

    @Deprecated
    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        String name = org.bukkit.ChatColor.stripColor(e.getView().getTitle());
        int slot = e.getRawSlot();
        Player player = (Player) e.getWhoClicked();
        User user = User.get(player);
        if (name.equals(PartyResource.GUI_NAME_PARTY_INFO_MENU)) {
            e.setCancelled(true);
            ItemStack clicked = e.getCurrentItem();
            if (clicked == null) return;
            if (clicked.isSimilar(PartyResource.getCancelMatchingItem())) {
                if (MatchingPool.isMatching(user.getParty())) {
                    MatchingPool.dequeue(user.getParty());
                    player.closeInventory();
                    playSound(player);
                }
                return;
            }
            if (clicked.isSimilar(PartyResource.getMatchItem())) {
                if (user.getParty().isOwner(user)) {
                    openGameListGUI(user);
                    playSound(player);
                }
                return;
            }
            if (clicked.isSimilar(PartyResource.getInfoItem())) {
                openPartyInfoMenu(user);
                playSound(player);
                return;
            }
            if (clicked.isSimilar(PartyResource.getInvitesItem())) {
                openPartyInviteMenu(user);
                playSound(player);
                return;
            }
            if (clicked.isSimilar(PartyResource.getLeaveItem())) {
                PartyManager.leaveParty(user);
                player.closeInventory();
                playSound(player);
                return;
            }
        }
        if (name.equals(ResourceIds.Font.GUI_CONTAINER_NOAH_GAMEMENU.content())) {
            e.setCancelled(true);
            if (slot >= 0 && slot < 27) {
                // Team Death Match
                MatchingPool.enqueue(TDMGame.class, user);
                player.closeInventory();
                playSound(player);
                return;
            }
            if (slot >= 27 && slot < 54) {
                // 1 vs 1 Death Match
                MatchingPool.enqueue(PDMGame.class, user);
                player.closeInventory();
                playSound(player);
                return;
            }
            return;
        }
        if (name.equals(ResourceIds.Font.GUI_CONTAINER_NOAH_PARTYMENU.content())) {
            e.setCancelled(true);
            ItemStack clicked = e.getCurrentItem();
            if (clicked == null) return;
            if (clicked.isSimilar(PartyResource.getMatchItem())) {
                openGameListGUI(user);
                playSound(player);
                return;
            }
            if (clicked.isSimilar(PartyResource.getInvitesItem())) {
                openPartyInviteMenu(user);
                playSound(player);
                return;
            }
        }
        if (name.equals(PartyResource.GUI_NAME_PARTY_INVITES)) {
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
            e.setCancelled(true);
            boolean accept = e.getClick() == ClickType.LEFT;
            String ownerName = e.getCurrentItem().getItemMeta().getDisplayName();
            for (Party party : user.getInvitedParties()) {
                if (ownerName.equals(party.getOwner().getName())) {
                    PartyManager.responseInvite(user, party, accept);
                    player.closeInventory();
                    playSound(player);
                    return;
                }
            }
        }
        if (name.equals(PartyResource.GUI_NAME_PARTY_INFO)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onFKey(PlayerSwapHandItemsEvent e) {
        User user = User.get(e.getPlayer());
        if (Noah.getLobby().getRegion().isInside(e.getPlayer()) && !user.isInGame()) {
            boolean shift = e.getPlayer().isSneaking();
            if (shift) {
                openPartyMainMenu(user);
                return;
            }
            if (user.getParty() == null) return;
            MatchingPool.dequeue(user.getParty());
        }
    }

    private void openGameListGUI(User user) {
        Inventory inventory = Bukkit.createInventory(null, 54, ResourceIds.Font.GUI_CONTAINER_NOAH_GAMEMENU);
        /* 기존 게임 목록 아이템 추가해주는 코드
        for (AbstractGame game : GameManager.getInstance().getRegisteredGames()) {
            final ItemStack stack = ItemStackUtil.get(game.getIcon(), game.name(), Component.text().append(
                Component.text("클릭시  ", NamedTextColor.GREEN),
                game.name().color(NamedTextColor.YELLOW),
                Component.text(" 게임 매칭 대기열에 입장합니다.", NamedTextColor.WHITE)
            ).build());
            inventory.addItem(stack);
        }
         */
        Player player = user.toPlayer();
        if (player != null) player.openInventory(inventory);
    }

    private void openPartyMainMenu(User user) {
        Inventory inventory;
        ItemStack invites = PartyResource.getInvitesItem();
        ItemStack match = PartyResource.getMatchItem();
        if (user.hasParty()) {
            inventory = Bukkit.createInventory(null, 9, PartyResource.GUI_NAME_PARTY_INFO_MENU);
            ItemStack info = PartyResource.getInfoItem();
            ItemStack leave = PartyResource.getLeaveItem();
            inventory.setItem(5, info);
            inventory.setItem(6, invites);
            inventory.setItem(7, leave);
            if (MatchingPool.isMatching(user.getParty())) {
                inventory.setItem(2, PartyResource.getCancelMatchingItem());
            } else {
                if (user.getParty().isOwner(user)) {
                    inventory.setItem(2, match);
                } else {
                    inventory.setItem(2, PartyResource.getCannotCancelMatchingItem());
                }
            }
        } else {
            inventory = Bukkit.createInventory(null, 9, ResourceIds.Font.GUI_CONTAINER_NOAH_PARTYMENU);
            inventory.setItem(2, match);
            inventory.setItem(6, invites);
        }
        Objects.requireNonNull(user.toPlayer()).openInventory(inventory);
    }

    private void openPartyInfoMenu(User user) {
        if (user.hasParty()) {
            Inventory inventory = Bukkit.createInventory(null, 54, PartyResource.GUI_NAME_PARTY_INFO);
            Party party = user.getParty();
            ItemStack owner = ItemStackUtil.get(HeadItemUtil.getHeadItemOffline(party.getOwner().toOfflinePlayer()), "<red>파티장", Collections.singletonList("<yellow>" + party.getOwner().getName()));
            inventory.addItem(owner);
            for (User member : party.getMembers()) {
                ItemStack i = ItemStackUtil.get(HeadItemUtil.getHeadItemOffline(member.toOfflinePlayer()), "<gold>파티원", Collections.singletonList("<yellow>" + member.getName()));
                inventory.addItem(i);
            }
            Objects.requireNonNull(user.toPlayer()).openInventory(inventory);
        }
    }

    private void openPartyInviteMenu(User user) {
        Inventory inventory = Bukkit.createInventory(null, 54, PartyResource.GUI_NAME_PARTY_INVITES);
        int idx = 0;
        List<Component> lore = new ArrayList<>();
        lore.add(ColorString.parse("<gold>좌클릭 <gray>- <white>수락"));
        lore.add(ColorString.parse("<gold>쉬프트 + 클릭 <gray>- <white>거절"));
        for (Party party : user.getInvitedParties()) {
            if (MatchingPool.isMatching(party)) continue;
            ItemStack i = HeadItemUtil.getHeadItemOffline(party.getOwner().toOfflinePlayer());
            ItemMeta m = i.getItemMeta();
            List<Component> l = new ArrayList<>();
            int index = 0;
            StringBuilder sb = new StringBuilder("<yellow>");
            for (User member : party.getMembers()) {
                sb.append(member.getName());
                if (++index == 3) {
                    l.add(ColorString.parse(sb.toString()));
                    index = 0;
                } else {
                    sb.append(", ");
                }
            }
            l.addAll(lore);
            ItemStackUtil.get(i, ColorString.parse(party.getOwner().getName()), l);
            inventory.setItem(idx++, i);
            if (idx >= 53) break;
        }
        Objects.requireNonNull(user.toPlayer()).openInventory(inventory);
    }

}
