package org.caramel.backas.noah.util;

import moe.caramel.caramellibrarylegacy.api.API;
import org.bukkit.entity.Player;
import org.caramel.backas.noah.SharedConstants;

public class ChannelItemUtil {

    public static final int CHANNEL_SLOT = 7;

    public static void giveItem(Player player) {
        if (SharedConstants.DEBUG) return;
        if (player == null) return;
        API.giveChannel(player, CHANNEL_SLOT);
    }
}
