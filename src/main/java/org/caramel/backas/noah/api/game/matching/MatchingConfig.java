package org.caramel.backas.noah.api.game.matching;

import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.api.config.Config;

import java.io.File;

public class MatchingConfig extends Config {

    public MatchingConfig() {
        super(new File(Noah.getInstance().getDataFolder(), "matching-messages.yml"));
    }

    public Value<String> getPartyIsMatching() {
        return new Value<>("PartyIsMatching", "이미 매칭 대기열에 입장된 상태입니다");
    }

    public Value<String> getGameNotFound() {
        return new Value<>("GameNotFound", "매칭할 게임을 찾을 수 없습니다");
    }

    public Value<String> getNotInQueue() {
        return new Value<>("NotInQueue", "파티가 매칭 대기열에 있지 않습니다");
    }
}
