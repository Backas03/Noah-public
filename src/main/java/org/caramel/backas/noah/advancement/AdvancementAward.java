package org.caramel.backas.noah.advancement;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface AdvancementAward {

    void give(Player player, AdvancementConstant constant);

}
