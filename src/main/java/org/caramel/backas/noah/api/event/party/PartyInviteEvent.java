package org.caramel.backas.noah.api.event.party;

import lombok.Getter;
import org.caramel.backas.noah.api.party.Party;
import org.caramel.backas.noah.api.user.User;

@Getter
public class PartyInviteEvent extends PartyEvent {

    private final User invitor;
    private final User invitee;

    public PartyInviteEvent(Party party, User invitor, User invitee) {
        super(party);
        this.invitor = invitor;
        this.invitee = invitee;
    }


}
