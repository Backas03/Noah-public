package org.caramel.backas.noah.api.event.user;

import org.caramel.backas.noah.api.user.User;

public class UserDataUnloadEvent extends UserEvent {

    public UserDataUnloadEvent(User user) {
        super(user);
    }

}
