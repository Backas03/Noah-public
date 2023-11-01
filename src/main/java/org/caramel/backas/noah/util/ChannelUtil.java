package org.caramel.backas.noah.util;

import moe.caramel.caramellibrarylegacy.api.API;
import org.bukkit.entity.Player;

public class ChannelUtil {

    public static final int CHANNEL_SLOT = 7;

    public static void giveItem(Player player) {
        if (player == null) return;
        API.giveChannel(player, CHANNEL_SLOT);
    }

    public static void removeItem(Player player) {
        if (player == null) return;
        player.getInventory().clear();
    }
}
