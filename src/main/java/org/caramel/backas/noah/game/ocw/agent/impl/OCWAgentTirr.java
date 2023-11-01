package org.caramel.backas.noah.game.ocw.agent.impl;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.caramel.backas.noah.game.ocw.OCWParticipant;
import org.caramel.backas.noah.game.ocw.agent.OCWAgent;
import org.caramel.backas.noah.game.ocw.agent.OCWAgentSkill;

public class OCWAgentTirr implements OCWAgent {
    @Override
    public Component getName() {
        return null;
    }

    @Override
    public String getWeaponKey() {
        return null;
    }

    @Override
    public ItemStack getIcon() {
        return null;
    }

    @Override
    public int getMaxHP() {
        return 0;
    }

    @Override
    public int getMaxMP() {
        return 70;
    }

    @Override
    public OCWAgentSkill getSkill() {
        return null;
    }

    private static final class SkillTirr implements OCWAgentSkill {
        @Override
        public int getCost() {
            return 45;
        }

        @Override
        public int getTickInterval() {
            return 0;
        }

        @Override
        public void onCast(Player player, OCWParticipant participant) {

        }
    }
}
