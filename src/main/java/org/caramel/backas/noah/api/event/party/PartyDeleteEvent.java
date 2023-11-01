package org.caramel.backas.noah.api.event.party;


import org.caramel.backas.noah.api.party.Party;

public class PartyDeleteEvent extends PartyEvent {

    public PartyDeleteEvent(Party party) {
        super(party);
    }

}
