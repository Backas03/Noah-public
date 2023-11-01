package org.caramel.backas.noah.api.game;

import org.caramel.backas.noah.api.user.User;

public interface IParticipant {

    User getUser();
    IGameTeam getTeam();

}
