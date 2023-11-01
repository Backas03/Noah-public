package org.caramel.backas.noah.resource;

import org.caramel.backas.noah.api.tier.Tier;
import org.caramel.backas.noah.deprecated.ResourceIds;

public class TierResource {

    public static String getChatFont(Tier tier) {
        switch (tier) {
            case STONE:
            case BRONZE:
            case SILVER:
            case GOLD:
            case DIAMOND:
            case RUBY:
            case KING:
            case THE_KING:
                return tier.getName();
        }
        return "UnRanked";
    }

    public static String getRankerChatFont() {
        return ResourceIds.Font.GAME_NOAH_RANK_8_TOP_CHAT;
    }

}
