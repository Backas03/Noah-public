package org.caramel.backas.noah.api.event.party;

import lombok.Getter;
import org.caramel.backas.noah.api.party.Party;
import org.caramel.backas.noah.api.user.User;

public class PartyLeaveEvent extends PartyEvent {

    @Getter
    private final User user;

    public PartyLeaveEvent(Party party, User user) {
        super(party);
        this.user = user;
    }

}
