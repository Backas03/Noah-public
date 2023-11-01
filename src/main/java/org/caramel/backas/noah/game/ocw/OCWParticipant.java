package org.caramel.backas.noah.game.ocw;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.caramel.backas.noah.game.ocw.agent.OCWAgent;
import org.caramel.backas.noah.game.ocw.agent.OCWPlayerAgent;
import org.caramel.backas.noah.user.User;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class OCWParticipant {

    private final User user;
    private final OCWTeamType teamType;
    private final Map<UUID, Double> damageDealt;
    private final Map<Class<? extends OCWAgent>, OCWPlayerAgent> agents;

    public int kill;
    public int death;
    public int assist;

    public int killStreak;

    public double totalDamageDealt;


    @Getter(AccessLevel.NONE)
    private OCWAgent agent;

    @Getter(AccessLevel.NONE)
    public int respawnTime;

    @Setter
    private boolean invincible;

    public OCWParticipant lastDamagedParticipant;

    public OCWParticipant(User user, OCWTeamType teamType) {
        this.user = user;
        this.teamType = teamType;
        this.damageDealt = new HashMap<>();
        this.agents = new HashMap<>();
        this.respawnTime = 0;
        this.kill = 0;
        this.death = 0;
        this.assist = 0;
        this.killStreak = 0;
        this.totalDamageDealt = 0;
        this.invincible = false;
        this.lastDamagedParticipant = null;
    }

    public double getDamageDealt(UUID victim) {
        return damageDealt.getOrDefault(victim, 0d);
    }

    public void addDamageDealt(UUID victim, double damage) {
        damageDealt.compute(victim, (k, v) -> v == null ? damage : v + damage);
    }

    public boolean isRespawning() {
        return respawnTime > 0;
    }

    public void setAgent(OCWAgent agent) {
        this.agent = agent;
    }

    public @Nullable OCWPlayerAgent getAgent() {
        if (agent == null) return null;
        return agents.get(agent.getClass());
    }
}
