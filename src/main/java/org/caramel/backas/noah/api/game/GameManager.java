package org.caramel.backas.noah.api.game;

import lombok.Getter;
import org.caramel.backas.noah.api.party.Party;

import java.util.LinkedHashSet;
import java.util.LinkedList;

public class GameManager {

    private static GameManager instance;

    public static GameManager getInstance() {
        if (instance == null) instance = new GameManager();
        return instance;
    }

    @Getter
    private final LinkedHashSet<AbstractGame> registeredGames = new LinkedHashSet<>();

    public void registerNewGame(AbstractGame game) {
        registeredGames.add(game);
    }

    public void unregisterGame(AbstractGame game) {
        for (GameChannel<?> gameChannel : game.getChannelPool().getChannels()) {
            gameChannel.over();
        }
        registeredGames.remove(game);
    }

    public AbstractGame getGame(String gameName) {
        for (AbstractGame game : registeredGames) {
            if (game.getName().equals(gameName)) {
                return game;
            }
        }
        return null;
    }

    public AbstractGame getGame(Class<? extends AbstractGame> gameType) {
        for (AbstractGame game : registeredGames) {
            if (game.getClass() == gameType) {
                return game;
            }
        }
        return null;
    }

    public AbstractGame getGame(int index) {
        return new LinkedList<>(registeredGames).get(index);
    }



}
