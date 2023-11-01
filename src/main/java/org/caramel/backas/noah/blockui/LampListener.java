package org.caramel.backas.noah.blockui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

public class LampListener implements Listener {

    @EventHandler
    public void onBlockDataChange(BlockRedstoneEvent event) {
        event.setNewCurrent(event.getOldCurrent());
    }
}
