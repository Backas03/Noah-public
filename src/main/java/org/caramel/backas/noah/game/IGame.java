package org.caramel.backas.noah.game;

import org.bukkit.entity.Player;
import org.caramel.backas.noah.user.User;
import java.util.Set;

public interface IGame {

    void onStart(Set<User> players) throws GameException;

    void onJoin(Player player) throws GameException;

    void onQuit(Player player) throws GameException;

    void onOver();

    int getTime();
}
