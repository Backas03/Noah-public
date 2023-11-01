package org.caramel.backas.noah.game.ocw;

import org.caramel.backas.noah.game.GameException;
import org.caramel.backas.noah.game.GameLauncher;
import org.caramel.backas.noah.game.IGame;
import org.caramel.backas.noah.game.IGameMap;
import org.caramel.backas.noah.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class OCWGameLauncher implements GameLauncher<GameOCW> {

    @Override
    public String getName() {
        return null;
    }

    @Override
    public GameOCW start(IGameMap map, Set<User> players) throws GameException {
        return null;
    }

    @Override
    public void onOver(IGame game) {

    }

    @Override
    public @NotNull IGameMap selectMap() throws GameException {
        return null;
    }
}
