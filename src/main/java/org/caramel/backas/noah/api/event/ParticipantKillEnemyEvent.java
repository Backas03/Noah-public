package org.caramel.backas.noah.api.event;

import kr.lostwar.fmj.api.weapon.Weapon;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.caramel.backas.noah.game.tdm.GameTDM;
import org.caramel.backas.noah.game.tdm.TDMParticipant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@Getter
@AllArgsConstructor
public class ParticipantKillEnemyEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final TDMParticipant attacker;
    private final TDMParticipant victim;
    private final Set<TDMParticipant> assister;
    private final GameTDM game;
    private final @Nullable Weapon weapon;

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
