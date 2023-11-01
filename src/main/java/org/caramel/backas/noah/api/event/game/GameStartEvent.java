package org.caramel.backas.noah.api.event.game;

import lombok.Getter;
import org.bukkit.event.Cancellable;
import org.caramel.backas.noah.api.game.AbstractGame;
import org.caramel.backas.noah.api.game.GameChannel;

public class GameStartEvent extends GameEvent implements Cancellable {

    @Getter
    private final GameChannel<?> channel;

    private boolean cancelled = false;

    public GameStartEvent(AbstractGame game, GameChannel<?> channel) {
        super(game);
        this.channel = channel;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

}
