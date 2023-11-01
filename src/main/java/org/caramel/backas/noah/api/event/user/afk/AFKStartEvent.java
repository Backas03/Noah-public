package org.caramel.backas.noah.api.event.user.afk;

import org.caramel.backas.noah.api.event.user.UserEvent;
import org.caramel.backas.noah.api.user.User;

public class AFKStartEvent extends UserEvent {

    public AFKStartEvent(User user) {
        super(user);
    }

}
