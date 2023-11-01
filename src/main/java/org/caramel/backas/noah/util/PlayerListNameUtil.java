package org.caramel.backas.noah.util;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class PlayerListNameUtil {

    public static void setDefault(Player player) {
        player.playerListName(Component.text(player.getName()));
    }
}
