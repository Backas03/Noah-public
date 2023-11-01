package org.caramel.backas.noah.api.event.party;

import lombok.Getter;
import org.caramel.backas.noah.api.party.Party;
import org.caramel.backas.noah.api.user.User;

@Getter
public class PartyInviteResponseEvent extends PartyEvent {

    private final User invitee;
    private final boolean accept;

    public PartyInviteResponseEvent(Party party, User invitee, boolean accept) {
        super(party);
        this.invitee = invitee;
        this.accept = accept;
    }
}
