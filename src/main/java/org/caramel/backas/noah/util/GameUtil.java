package org.caramel.backas.noah.util;

import org.caramel.backas.noah.api.game.AbstractGame;
import org.caramel.backas.noah.api.user.User;

public class GameUtil {

    public static boolean gameEquals(User user1, User user2, boolean checkChannel) {
        if (!user1.isInGame() || !user2.isInGame()) return false;
        boolean compare = true;
        if (checkChannel) compare = user1.getGameData().getChannel().equals(user2.getGameData().getChannel());
        return user1.getGameData().getGame().equals(user2.getGameData().getGame()) && compare;

    }

    public static boolean gameEquals(User user, Class<? extends AbstractGame> clazz) {
        return user.isInGame() && clazz.isAssignableFrom(user.getGameData().getGame().getClass());
    }

}
