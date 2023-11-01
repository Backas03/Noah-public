package org.caramel.backas.noah.game.ocw.agent;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.caramel.backas.noah.game.ocw.OCWParticipant;
import org.caramel.backas.noah.util.TimeUtil;

public class OCWPlayerAgent {

    private final OCWAgent agent;
    private int mana;
    private long casted;

    public OCWPlayerAgent(OCWAgent agent) {
        this.agent = agent;
        this.mana = 0;
        this.casted = 0;
    }

    public OCWAgent getInfo() {
        return agent;
    }

    public void addMana(int amount) {
        mana = Math.min(agent.getMaxMP(), mana + amount);
    }

    public void removeMana(int amount) {
        mana = Math.max(0, mana - amount);
    }

    public void setMana(int mana) {
        this.mana = Math.min(0, Math.max(agent.getMaxMP(), mana));
    }

    public void setManaUnsafe(int mana) {
        this.mana = mana;
    }

    public void onSkillCastActionHooked(Player player, OCWParticipant participant) {
        int cost = agent.getSkill().getCost();
        if (mana - cost < 0) {
            player.sendMessage(Component.text("마나가 부족해영"));
            return;
        }
        long casted = TimeUtil.millisToTicks(this.casted);
        long current = TimeUtil.millisToTicks(System.currentTimeMillis());
        long castable = casted + agent.getSkill().getTickInterval();
        if (castable > current) {
            float left = (castable - current) / 20f; // ms to tick
            player.sendMessage(Component.text("스킬 시전 가능까지 " + String.format("%.2f초", left) + " 남았어여"));
            return;
        }
        this.casted = System.currentTimeMillis();
        mana -= cost;
        agent.getSkill().onCast(player, participant);
    }
}
