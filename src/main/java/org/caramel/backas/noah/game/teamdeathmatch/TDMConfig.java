package org.caramel.backas.noah.game.teamdeathmatch;

import org.caramel.backas.noah.game.deathmatch.DeathMatchConfig;

public class TDMConfig extends DeathMatchConfig {

    public TDMConfig() {
        super("team-death-match-config.yml");
    }

    @Override
    public Value<Integer> getTime() {
        return new Value<>("Time", 420);
    }
}
