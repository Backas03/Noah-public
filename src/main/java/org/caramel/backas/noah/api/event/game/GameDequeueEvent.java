package org.caramel.backas.noah.api.event.game;

import lombok.Getter;
import org.caramel.backas.noah.api.game.AbstractGame;
import org.caramel.backas.noah.api.party.Party;

public class GameDequeueEvent extends GameEvent {

    @Getter
    private final Party party;

    public GameDequeueEvent(Party party, AbstractGame game) {
        super(game);
        this.party = party;
    }
}
