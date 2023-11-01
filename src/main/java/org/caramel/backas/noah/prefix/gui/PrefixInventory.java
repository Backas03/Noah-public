package org.caramel.backas.noah.prefix.gui;

import moe.caramel.acacia.api.inventory.page.AbstractPageInventory;
import moe.caramel.acacia.api.inventory.page.Content;
import moe.caramel.acacia.api.inventory.page.model.ArrayedPageInventory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.deprecated.ResourceIds;
import org.caramel.backas.noah.prefix.Prefix;
import org.caramel.backas.noah.prefix.PrefixData;
import org.caramel.backas.noah.prefix.gui.content.PrefixContent;
import org.caramel.backas.noah.user.User;
import org.caramel.backas.noah.util.ItemStackUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PrefixInventory extends ArrayedPageInventory {

    public static final int INVENTORY_SIZE = 54;
    public static final int CONTENT_SIZE = 45;

    public static void open(Player player) {
        User.get(player).getDataContainer().getOrLoadAsync(PrefixData.class).thenApply(prefixData -> {
            List<PrefixContent> items = new ArrayList<>();
            if (prefixData.getNamespacedKeys() != null) {
                for (String namespaceKey : prefixData.getNamespacedKeys()) {
                    ItemStack item = ItemStackUtil.get(new ItemStack(Material.PAPER), Component.text(namespaceKey), Prefix.getComponent(namespaceKey));
                    if (namespaceKey.equals(prefixData.getCurrentNamespacedKey())) {
                        item.editMeta(itemMeta -> {
                            itemMeta.displayName(Component.text(namespaceKey).style(ResourceIds.Styles.RED_NO_ITALIC));
                            List<Component> lore = itemMeta.lore();
                            lore.add(Component.text(" * ", ResourceIds.Styles.RED_NO_ITALIC).append(Component.text("현재 착용중인 칭호입니다.", ResourceIds.Styles.GRAY_NO_ITALIC)));
                            itemMeta.lore(lore);
                        });
                    } else {
                        item.editMeta(itemMeta -> {
                            itemMeta.displayName(Component.text(namespaceKey).style(ResourceIds.Styles.YELLOW_NO_ITALIC));
                            List<Component> lore = itemMeta.lore();
                            lore.add(Component.text(" >> ", ResourceIds.Styles.DARK_GRAY_NO_ITALIC).append(Component.text("클릭 하여 칭호를 착용합니다.", ResourceIds.Styles.GRAY_NO_ITALIC)));
                            itemMeta.lore(lore);
                        });
                    }
                    items.add(new PrefixContent(item, namespaceKey));
                }
            }
            PrefixInventory inventory = new PrefixInventory(items);
            Bukkit.getScheduler().runTask(Noah.getInstance(), () -> player.openInventory(inventory.inventory));
            return prefixData;
        });
    }

    protected PrefixInventory(@NotNull List<? extends @NotNull ItemStack> contents) {
        super(INVENTORY_SIZE, CONTENT_SIZE, contents);
        setItem(52, new Content(ItemStackUtil.get(Material.STICK, Component.text("이전 페이지로", NamedTextColor.RED)), true, (event, inv, clicked) -> inv.previousPage())); // 이전 페이지 아이템
        setItem(53, new Content(ItemStackUtil.get(Material.BLAZE_ROD, Component.text("다음 페이지로", NamedTextColor.GREEN)), true, (event, inv, clicked) -> inv.nextPage())); // 다음 페이지 아이템
        setItem(45, new Content(ItemStackUtil.get(Material.CHEST, Component.text("칭호 장착 해제", NamedTextColor.RED), Component.text("클릭하여 칭호를 장착 해제합니다", NamedTextColor.GRAY)),
                true,
                (event, inventory1, clicked) -> {
                    if (!(event.getWhoClicked() instanceof Player player)) return;
                    player.closeInventory();
                    User.get(player).getDataContainer().getOrLoadAsync(PrefixData.class).thenApply(prefixData -> {
                        prefixData.setPrefix(null);
                        player.sendMessage(Component.text("[칭호] ", NamedTextColor.GREEN).append(Component.text("칭호가 장착 해제되었습니다.", NamedTextColor.WHITE)));
                        return prefixData;
                    });
                })
        );
    }


    @Override
    protected @Nullable Component createTitle() {
        return Component.text("칭호 목록 [" + getCurrentPage() + "/" + getMaxPage() + "]");
    }

    @Override
    protected void onClick(@NotNull InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
