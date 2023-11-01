package org.caramel.backas.noah.api.event.party;

import lombok.Getter;
import org.caramel.backas.noah.api.party.Party;
import org.caramel.backas.noah.api.user.User;

public class PartyCreateEvent extends PartyEvent {

    @Getter
    private final User creator;

    public PartyCreateEvent(Party party, User creator) {
        super(party);
        this.creator = creator;
    }
}
