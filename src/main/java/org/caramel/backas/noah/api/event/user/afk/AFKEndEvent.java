package org.caramel.backas.noah.api.event.user.afk;

import lombok.Getter;
import org.caramel.backas.noah.api.event.user.UserEvent;
import org.caramel.backas.noah.api.user.User;

public class AFKEndEvent extends UserEvent {

    @Getter
    private final int second;

    public AFKEndEvent(User user, int second) {
        super(user);
        this.second = second;
    }

}
