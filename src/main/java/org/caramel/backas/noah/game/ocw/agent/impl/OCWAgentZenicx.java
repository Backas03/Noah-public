package org.caramel.backas.noah.game.ocw.agent.impl;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.caramel.backas.noah.game.ocw.OCWParticipant;
import org.caramel.backas.noah.game.ocw.agent.OCWAgent;
import org.caramel.backas.noah.game.ocw.agent.OCWAgentSkill;

public class OCWAgentZenicx implements OCWAgent {

    @Override
    public Component getName() {
        return Component.text("제닉스");
    }

    @Override
    public String getWeaponKey() {
        return "Reaper";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.STONE);
    }

    @Override
    public int getMaxHP() {
        return 0;
    }

    @Override
    public int getMaxMP() {
        return 100;
    }

    @Override
    public OCWAgentSkill getSkill() {
        return new SkillZenicx();
    }

    private static final class SkillZenicx implements OCWAgentSkill {

        @Override
        public int getCost() {
            return 35;
        }

        @Override
        public int getTickInterval() {
            return 60;
        }

        @Override
        public void onCast(Player player, OCWParticipant participant) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 2, 4));
        }
    }

}
