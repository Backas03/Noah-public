package org.caramel.backas.noah.game.tdm;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.caramel.backas.noah.Lobby;
import org.caramel.backas.noah.game.ITeamType;

import java.util.*;

public class TDMTeam {

    @Getter
    private final Type type;

    private final Map<UUID, TDMParticipant> participants;
    public int kills;
    public int assists;
    public double totalDamageDealt;


    public TDMTeam(Type type) {
        this.type = type;
        this.participants = new HashMap<>();
        kills = 0;
        assists = 0;
        totalDamageDealt = 0;
    }

    public void initParticipant(TDMParticipant participant) {
        participants.put(participant.getUser().getUniqueId(), participant);
    }

    public boolean insideEmpty() {
        boolean value = true;
        for (TDMParticipant participant : participants.values()) {
            Optional<Player> player = participant.getUser().getPlayer();
            if (player.isPresent() && !player.get().getWorld().getName().equals(Lobby.getWorld().getName())) {
                value = false;
                break;
            }
        }
        return value;
    }

    public TDMParticipant getParticipant(UUID uniqueId) {
        return participants.get(uniqueId);
    }

    public Collection<TDMParticipant> getParticipants() {
        return participants.values();
    }

    @AllArgsConstructor
    public enum Type implements ITeamType {
        RED(NamedTextColor.RED, "레드 팀"),
        BLACK(NamedTextColor.DARK_GRAY, "블랙 팀");

        public Type side() {
            if (this == RED) return BLACK;
            else return RED;
        }

        @Getter
        private final TextColor color;
        private final String name;

        public Component getName() {
            return Component.text(name, color);
        }

        public Component getUncoloredName() {
            return Component.text(name);
        }
    }
}
