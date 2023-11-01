package org.caramel.backas.noah.game.ocw;

import lombok.Getter;
import org.caramel.backas.noah.user.User;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class OCWTeam {

    private final OCWTeamType teamType;
    private final Map<UUID, OCWParticipant> participants;

    public int kills;
    public int assists;

    public double totalDamageDealt;

    public OCWTeam(OCWTeamType type) {
        this.teamType = type;
        this.participants = new HashMap<>();
        this.kills = 0;
        this.assists = 0;
        this.totalDamageDealt = 0;
    }

    public void initUserParticipant(User user) {
        this.participants.put(user.getUniqueId(), new OCWParticipant(user, teamType));
    }
}
