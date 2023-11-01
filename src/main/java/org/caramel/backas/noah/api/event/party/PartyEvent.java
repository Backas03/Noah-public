package org.caramel.backas.noah.api.event.party;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.caramel.backas.noah.api.party.Party;
import org.jetbrains.annotations.NotNull;

@Getter
@AllArgsConstructor
public class PartyEvent extends Event {

    private final Party party;

    private static final HandlerList handlers = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
