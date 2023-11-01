package org.caramel.backas.noah.util;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import java.util.UUID;

public class OfflinePlayerUtil {

    public static OfflinePlayer getFromOffline(String name) {
        return Bukkit.getOfflinePlayer(name);
    }

    public static OfflinePlayer getFromOffline(UUID uuid) {
        return Bukkit.getOfflinePlayer(uuid);
    }

    public static OfflinePlayer getFromOnline(String name) {
        return Bukkit.getOfflinePlayer(name);
    }
}
