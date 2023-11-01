package org.caramel.backas.noah.game.deathmatch;

import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.api.config.Config;
import org.caramel.backas.noah.api.config.mapper.GunInventorySizeMapper;
import org.caramel.backas.noah.api.config.mapper.SetMapper;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DeathMatchConfig extends Config {

    public DeathMatchConfig(String fileName) {
        super(new File(Noah.getInstance().getDataFolder(), fileName));
        load();
    }

    public Value<Integer> getTime() {
        return new Value<>("Time", 600);
    }

    public Value<Double> getDamageToAssist() {
        return new Value<>("DamageToAssist", 40D);
    }

    public Value<String> getKillLogFormat() {
        return new Value<>("KillLogFormat", "%attacker% %kill_streak% <gray>killed %victim% <gray>with <white>%weapon%");
    }

    public Value<Integer> getRespawnCoolTime() {
        return new Value<>("RespawnCoolTime", 3);
    }

    public Value<String> getRespawnTitleFormat() {
        return new Value<>("RespawnTitleFormat", "<red>리스폰 까지 <gray>%second%초");
    }

    public Value<String> getRespawnSubTitleFormat() {
        return new Value<>("RespawnSubTitleFormat", "<dark_gray>F키 <gray>- 총 선택");
    }

    public Value<Integer> getRespawnGodDuration() {
        return new Value<>("RespawnGodDuration", 3);
    }

    public Value<String> getRespawnSuccessTitle() {
        return new Value<>("RespawnSuccessTitle", "<green>리스폰 완료!");
    }

    public Value<String> getRespawnSuccessSubTitle() {
        return new Value<>("RespawnSuccessSubTitle", "<gray>지금부터 3초 동안 적의 공격을 무시합니다");
    }

    public Value<String> getRespawnGodEndTitle() {
        return new Value<>("RespawnGodEndTitle", "");
    }

    public Value<String> getRespawnGodEndSubTitle() {
        return new Value<>("RespawnGodEndSubTitle", "<gray>공격 무시 상태가 해제 되었습니다");
    }

    public Value<String> getSubWeaponKey() {
        return new Value<>("SubWeaponKey", "flatline");
    }

    public Value<Integer> getGunInventoryRows() {
        return new Value<>("GunInventoryRows", 1, new GunInventorySizeMapper(getWeaponKeys().get().size()));
    }

    public Value<String> getKillStreakTitleFormat() {
        return new Value<>("KillStreakTitleFormat", "%kill_streak%");
    }

    public Value<String> getKillStreakSubTitleFormat() {
        return new Value<>("KillStreakSubTitleFormat", "");
    }

    public Value<Set<String>> getWeaponKeys() {
        return new Value<>("WeaponKeys", new HashSet<>(Collections.singletonList("Reaper")), new SetMapper<>());
    }
}
