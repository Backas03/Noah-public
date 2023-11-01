package org.caramel.backas.noah.api.event.party;

import lombok.Getter;
import org.caramel.backas.noah.api.party.Party;
import org.caramel.backas.noah.api.user.User;

public class PartyInviteRemoveEvent extends PartyEvent {

    @Getter
    private final User user;

    public PartyInviteRemoveEvent(User user, Party party) {
        super(party);
        this.user = user;
    }


}
