package org.caramel.backas.noah.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.caramel.backas.noah.user.User;
import org.jetbrains.annotations.NotNull;

public class UserLoadEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final User user;

    public User getUser() {
        return user;
    }

    public UserLoadEvent(User user) {
        this.user = user;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}