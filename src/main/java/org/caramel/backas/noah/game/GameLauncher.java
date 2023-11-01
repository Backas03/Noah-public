package org.caramel.backas.noah.game;

import org.caramel.backas.noah.user.User;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public interface GameLauncher<T extends IGame> {

    String getName();

    T start(IGameMap map, Set<User> players) throws GameException;

    void onOver(IGame game);

    @NotNull IGameMap selectMap() throws GameException;

    Map<Class<?>, GameLauncher<?>> GAME_LAUNCHER_MAP = new HashMap<>();

    static <T extends IGame> void register(Class<T> gameType, GameLauncher<T> launcher) {
        GAME_LAUNCHER_MAP.put(gameType, launcher);
    }

    @SuppressWarnings("unchecked")
    static <T extends IGame> GameLauncher<T> getLauncher(Class<T> gameType) {
        return (GameLauncher<T>) GAME_LAUNCHER_MAP.get(gameType);
    }

    static GameLauncher<?> random() {
        int index = (int) (Math.random() * (GAME_LAUNCHER_MAP.size() - 1));
        return (GameLauncher<?>) GAME_LAUNCHER_MAP.values().toArray()[index];
    }
}
