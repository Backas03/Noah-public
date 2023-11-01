package org.caramel.backas.noah.api.event.game;

import lombok.Getter;
import org.caramel.backas.noah.api.game.AbstractGame;
import org.caramel.backas.noah.api.party.Party;

public class GameEnqueueEvent extends GameEvent {

    @Getter
    private final Party party;

    public GameEnqueueEvent(Party party, AbstractGame game) {
        super(game);
        this.party = party;
    }

}
