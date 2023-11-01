package org.caramel.backas.noah.api.event.user;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.caramel.backas.noah.api.user.User;
import org.jetbrains.annotations.NotNull;

public class UserEvent extends Event {

    @Getter
    private final User user;

    public UserEvent(User user) {
        this.user = user;
    }

    private static final HandlerList handlers = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
