package org.caramel.backas.noah.api.game;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.api.event.game.GameOverEvent;
import org.caramel.backas.noah.api.event.game.GameStartEvent;
import org.caramel.backas.noah.api.party.Party;
import org.caramel.backas.noah.api.user.model.CurrentGameData;

import java.util.*;

@Getter
public abstract class GameChannel<T extends GameMap> {

    private final AbstractGame game;
    private final T gameMap;
    protected Map<Party, IGameTeam> players;

    protected abstract void onStart(AbstractGame game, Map<Party, IGameTeam> players);
    protected abstract void onOver(AbstractGame game, Map<Party, IGameTeam> players);
    public abstract boolean canStart();

    public GameChannel(AbstractGame game, T gameMap) {
        this.game = game;
        this.gameMap = gameMap;
    }

    public boolean isEmpty() {
        return players == null;
    }


    public void start(Map<Party, IGameTeam> players) {
        if (!canStart()) {
            Noah.getInstance().getDebugLogger().warn("게임을 시작할 수 없는 채널입니다. [" + getName() + "]");
            return;
        }
        for (Party party : players.keySet()) {
            party.getAllMembers().forEach(user -> user.setGameData(new CurrentGameData(game, this)));
        }
        this.players = players;
        onStart(game, players);
        Bukkit.getPluginManager().callEvent(new GameStartEvent(game, this));
    }

    public void over() {
        if (players == null) return;
        for (Party party : players.keySet()) {
            party.getAllMembers().forEach(user -> user.setGameData(null));
        }
        onOver(game, players);
        Bukkit.getPluginManager().callEvent(new GameOverEvent(game, this));
    }

    public String getName() {
        return gameMap.getName();
    }

    @Override
    public String toString() {
        return getName();
    }
}
