package org.caramel.backas.noah.api.event.game;

import lombok.Getter;
import lombok.NonNull;
import org.caramel.backas.noah.api.game.AbstractGame;
import org.caramel.backas.noah.api.game.GameChannel;

public class GameOverEvent extends GameEvent {

    @Getter
    private final GameChannel<?> channel;

    public GameOverEvent(@NonNull AbstractGame getGame, GameChannel<?> channel) {
        super(getGame);
        this.channel = channel;
    }

}
