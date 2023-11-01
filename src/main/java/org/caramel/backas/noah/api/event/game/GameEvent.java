package org.caramel.backas.noah.api.event.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.caramel.backas.noah.api.game.AbstractGame;
import org.jetbrains.annotations.NotNull;

@Getter
@AllArgsConstructor
public class GameEvent extends Event {

    private final AbstractGame game;

    private static final HandlerList handlers = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
