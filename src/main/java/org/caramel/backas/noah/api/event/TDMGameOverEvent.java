package org.caramel.backas.noah.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.caramel.backas.noah.game.tdm.GameTDM;
import org.jetbrains.annotations.NotNull;


public class TDMGameOverEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final GameTDM game;

    public GameTDM getGame() {
        return game;
    }

    public TDMGameOverEvent(GameTDM game) {
        this.game = game;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
