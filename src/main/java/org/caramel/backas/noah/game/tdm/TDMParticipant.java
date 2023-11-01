package org.caramel.backas.noah.game.tdm;

import kr.lostwar.fmj.api.weapon.Weapon;
import lombok.Getter;
import org.caramel.backas.noah.user.User;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TDMParticipant {

    private final User user;
    @Getter
    private final TDMTeam.Type teamType;
    @Getter
    private final Map<UUID, Double> damageDealt;

    public int kill;
    public int death;
    public int assist;
    public double totalDamageDealt;
    public int killStreak;
    public boolean godMode;
    public int respawnTime;
    public Weapon nextWeapon;


    public TDMParticipant(User user, TDMTeam.Type teamType) {
        this.user = user;
        this.teamType = teamType;
        kill = 0;
        death = 0;
        assist = 0;
        killStreak = 0;
        respawnTime = 0;
        nextWeapon = null;
        godMode = false;
        damageDealt = new HashMap<>();
    }

    public int getKillStreak() {
        return Math.min(TDMGameOption.MAX_KILL_STREAK, killStreak);
    }

    public double getDamageDealt(UUID victimUUID) {
        return damageDealt.getOrDefault(victimUUID, 0D);
    }

    public boolean isRespawning() {
        return respawnTime > 0;
    }

    public void addDamageDealt(UUID victimUUID, double damage) {
        damageDealt.compute(victimUUID, (k, v) -> v == null ? damage : v + damage);
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return user.getName();
    }
}
