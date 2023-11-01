package org.caramel.backas.noah.game.ocw.agent;

import org.bukkit.entity.Player;
import org.caramel.backas.noah.game.ocw.OCWParticipant;

public interface OCWAgentSkill {

    int getCost();

    int getTickInterval();

    void onCast(Player player, OCWParticipant participant);
}
