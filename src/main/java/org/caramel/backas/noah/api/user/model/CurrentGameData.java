package org.caramel.backas.noah.api.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.caramel.backas.noah.api.game.AbstractGame;
import org.caramel.backas.noah.api.game.GameChannel;

@Getter
@Setter
@AllArgsConstructor
public class CurrentGameData {

    private AbstractGame game;
    private GameChannel<?> channel;

    public boolean isEnqueue() {
        return channel == null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CurrentGameData compare) {
            return channel != null ? compare.game.equals(game) && compare.channel.equals(channel) : compare.game.equals(game) && compare.channel == null;
        }
        return false;
    }

    @Override
    public String toString() {
        return "CurrentGameData{Game=" + game + ", Channel=" + channel + "}";
    }
}
