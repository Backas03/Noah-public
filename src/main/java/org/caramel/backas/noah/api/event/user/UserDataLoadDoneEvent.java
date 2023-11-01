package org.caramel.backas.noah.api.event.user;

import org.caramel.backas.noah.api.user.User;

public class UserDataLoadDoneEvent extends UserEvent {

    public UserDataLoadDoneEvent(User user) {
        super(user);
    }

}
