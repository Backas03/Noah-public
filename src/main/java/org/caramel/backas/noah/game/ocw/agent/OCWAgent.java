package org.caramel.backas.noah.game.ocw.agent;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

public interface OCWAgent {

    Component getName();

    String getWeaponKey();

    ItemStack getIcon();

    int getMaxHP();

    int getMaxMP();

    OCWAgentSkill getSkill();
}
