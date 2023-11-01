package org.caramel.backas.noah.game.personaldeathmatch;

import org.caramel.backas.noah.game.deathmatch.DeathMatchConfig;

public class PDMConfig extends DeathMatchConfig {

    public PDMConfig() {
        super("1vs1-death-match-config.yml");
    }

    @Override
    public Value<Integer> getTime() {
        return new Value<>("Time", 300);
    }
}
