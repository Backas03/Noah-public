package org.caramel.backas.noah.game.personaldeathmatch;

import org.caramel.backas.noah.api.game.AbstractGame;
import org.caramel.backas.noah.api.game.IGameTeam;
import org.caramel.backas.noah.api.party.Party;
import org.caramel.backas.noah.game.deathmatch.DeathMatchChannel;
import org.caramel.backas.noah.game.deathmatch.DeathMatchGameMap;

import java.util.Map;

public class PDMChannel extends DeathMatchChannel<DeathMatchGameMap> {

    public PDMChannel(PDMGame game, DeathMatchGameMap gameMap) {
        super(game, gameMap);
    }

    @Override
    protected void onOver(AbstractGame game, Map<Party, IGameTeam> players) {
        getParticipants().values().forEach(participant ->
                participant.getUser().sendMessage("<green>Rating <gray>| <red>1 VS 1 데스매치는 MMR 시스템을 적용하지 않습니다."));
        super.onOver(game, players);
    }
}
