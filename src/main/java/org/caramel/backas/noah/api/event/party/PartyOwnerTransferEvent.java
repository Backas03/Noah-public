package org.caramel.backas.noah.api.event.party;

import lombok.Getter;
import org.caramel.backas.noah.api.party.Party;
import org.caramel.backas.noah.api.user.User;

@Getter
public class PartyOwnerTransferEvent extends PartyEvent {

    private final User oldOwner;
    private final User newOwner;

    public PartyOwnerTransferEvent(Party party, User oldOwner, User newOwner) {
        super(party);
        this.oldOwner = oldOwner;
        this.newOwner = newOwner;
    }

}
