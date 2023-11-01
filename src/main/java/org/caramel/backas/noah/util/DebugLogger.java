package org.caramel.backas.noah.util;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

@AllArgsConstructor
public class DebugLogger {

    private final Plugin plugin;

    public void info(String msg) {
        Bukkit.getConsoleSender().sendMessage(ColorString.parse("<green>[" + plugin.getName() + "] <white>" +  msg));
    }

    public void warn(String msg) {
        Bukkit.getConsoleSender().sendMessage(ColorString.parse("<gold>[" + plugin.getName() + "] <yellow>" + msg));
    }

    public void error(String msg) {
        Bukkit.getConsoleSender().sendMessage(ColorString.parse("<dark_red>[" + plugin.getName() + "] <red>" + msg));
    }
}
