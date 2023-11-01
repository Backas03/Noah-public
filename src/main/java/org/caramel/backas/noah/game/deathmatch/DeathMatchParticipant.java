package org.caramel.backas.noah.game.deathmatch;

import kr.lostwar.fmj.api.weapon.Weapon;
import lombok.Getter;
import lombok.Setter;
import org.caramel.backas.noah.api.game.IParticipant;
import org.caramel.backas.noah.api.game.rating.Rating;
import org.caramel.backas.noah.api.user.User;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class DeathMatchParticipant implements IParticipant {

    private final UUID uniqueId;
    private final DeathMatchTeamType team;
    private final Map<UUID, Double> damageDealt;

    private int kills;
    private int deaths;
    private int assists;
    private double totalDamageDealt;

    private int respawnTime;
    private boolean godMode;
    private Weapon nextWeapon;
    private int killStreak;

    public boolean isRespawning() {
        return respawnTime > 0;
    }

    public DeathMatchParticipant(UUID uuid, DeathMatchTeamType teamType) {
        uniqueId = uuid;
        team = teamType;
        kills = 0;
        deaths = 0;
        assists = 0;
        totalDamageDealt = 0;
        respawnTime = 0;
        killStreak = 0;
        nextWeapon = null;
        godMode = false;
        damageDealt = new HashMap<>();
    }

    public int getKillStreak() {
        return Math.min(7, killStreak);
    }

    public User getUser() {
        return User.get(uniqueId);
    }

    public void addDamageDealt(UUID victimUUID, double damage) {
        damageDealt.compute(victimUUID, (k, v) -> v == null ? damage : v + damage);
    }

    public double getDamageDealt(UUID victimUUID) {
        return damageDealt.getOrDefault(victimUUID, 0D);
    }

    @Override
    public String toString() {
        return getUser().getName();
    }
}