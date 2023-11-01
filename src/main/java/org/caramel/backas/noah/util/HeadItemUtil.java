package org.caramel.backas.noah.util;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class HeadItemUtil {

    public static ItemStack getHeadItemOffline(String ownerName) {
        return getHeadItemOffline(OfflinePlayerUtil.getFromOffline(ownerName));
    }

    public static ItemStack getHeadItemOffline(OfflinePlayer offlinePlayer) {
        ItemStack i = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta sm = (SkullMeta) i.getItemMeta();
        sm.setOwningPlayer(offlinePlayer);
        i.setItemMeta(sm);
        return i;
    }

    public static ItemStack getHeadItemOnline(String ownerName) {
        return getHeadItemOffline(OfflinePlayerUtil.getFromOnline(ownerName));
    }
}
