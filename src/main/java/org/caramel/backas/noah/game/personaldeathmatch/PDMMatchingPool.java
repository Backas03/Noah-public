package org.caramel.backas.noah.game.personaldeathmatch;

import org.caramel.backas.noah.api.game.AbstractGame;
import org.caramel.backas.noah.api.game.IGameTeam;
import org.caramel.backas.noah.api.game.matching.MatchingPool;
import org.caramel.backas.noah.api.party.Party;
import org.caramel.backas.noah.game.deathmatch.DeathMatchTeamType;

import java.util.*;

public class PDMMatchingPool extends MatchingPool {

    public PDMMatchingPool(AbstractGame game, int maxMMRDiff, int mmrPerSecond) {
        super(game, maxMMRDiff, mmrPerSecond);
    }

    @Override
    public boolean canEnqueue(Party party) {
        if (party.getAllMembers().size() != 1) {
            party.broadcastTitle("<red>매칭 대기열 입장 실패", "<gray>1인 파티로만 입장할 수 있습니다", 0, 40, 0);
            return false;
        }
        return true;
    }

    @Override
    protected Map<Party, IGameTeam> distribute(List<Party> parties) {
        if (parties.size() >= 2) {
            IGameTeam[] teams = new IGameTeam[] {DeathMatchTeamType.RED, DeathMatchTeamType.BLACK};
            List<IGameTeam> shuffle = Arrays.asList(teams);
            Collections.shuffle(shuffle);
            Map<Party, IGameTeam> result = new HashMap<>();
            for (int i=0; i<2; i++) {
                result.put(parties.get(i), shuffle.get(i));
            }
            return result;
        }

        return null;
    }
}
