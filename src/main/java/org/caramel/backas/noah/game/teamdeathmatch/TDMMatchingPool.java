package org.caramel.backas.noah.game.teamdeathmatch;

import org.bukkit.Bukkit;
import org.caramel.backas.noah.api.game.AbstractGame;
import org.caramel.backas.noah.api.game.IGameTeam;
import org.caramel.backas.noah.api.game.matching.MatchingPool;
import org.caramel.backas.noah.api.party.Party;
import org.caramel.backas.noah.api.user.User;
import org.caramel.backas.noah.game.deathmatch.DeathMatchTeamType;

import java.util.*;

public class TDMMatchingPool extends MatchingPool {

    public TDMMatchingPool(AbstractGame game, int maxMMRDiff, int mmrPerSecond) {
        super(game, maxMMRDiff, mmrPerSecond);
    }

    @Override
    public boolean canEnqueue(Party party) {
        if (party.getAllMembers().size() >= 4) {
            party.broadcastTitle("<red>매칭 대기열 입장 실패", "<gray>4명 이상의 파티원으로 입장하실 수 없습니다", 0, 40, 20);
            return false;
        }
        return true;
    }

    @Override
    protected Map<Party, IGameTeam> distribute(List<Party> parties) {
        int cmp = Bukkit.getOnlinePlayers().size();
        for (User user : User.getAll()) {
            if (user.getAfkCache().isAFK()) {
                cmp--;
                continue;
            }
            if (user.getGameData() != null && user.getGameData().getChannel() != null) cmp--;
        }

        int m = 4; // max team size
        int k = Math.min(cmp / 2, m); // team size

        Map<Party, IGameTeam> result = new HashMap<>();

        // greedy algorithm (queue first, mmr distribution not taken into account)
        IGameTeam[] teams = new IGameTeam[] {DeathMatchTeamType.RED, DeathMatchTeamType.BLACK};
        List<IGameTeam> shuffle = Arrays.asList(teams);
        Collections.shuffle(shuffle);
        List<Party> queue = new LinkedList<>(parties);
        int[] sizes = new int[] {0, 0};
        for (int i=0; i<2; i++) {
            int size = 0;
            DeathMatchTeamType team = (DeathMatchTeamType) teams[i];
            for (Party party : queue) {
                int n = party.getAllMembers().size();
                int max = k - size - n;
                if (max >= 0) {
                    size += n;
                    sizes[i] += n;
                    result.put(party, team);
                    if (max == 0) break;
                }
            }
            queue.removeIf(result::containsKey);
        }
        if (k == 1 || sizes[0] != sizes[1] || sizes[0] != k) return null;

        return result;
    }

}
