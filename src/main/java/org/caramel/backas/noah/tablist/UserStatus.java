package org.caramel.backas.noah.tablist;

import org.bukkit.entity.Player;
import org.caramel.backas.noah.api.user.User;

public enum UserStatus {

    ONLINE,
    AFK,
    IN_QUEUE,
    IN_GAME;

    public static UserStatus getStatus(Player player) {
        User user = User.get(player);
        if (user.isInGame()) {
            return IN_GAME;
        } else if (user.isInQueue()) {
            return IN_QUEUE;
        } else if (user.getAfkCache().isAFK()){
            return AFK;
        } else {
            return ONLINE;
        }
    }

}
