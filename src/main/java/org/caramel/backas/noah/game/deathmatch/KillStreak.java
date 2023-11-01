package org.caramel.backas.noah.game.deathmatch;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.caramel.backas.noah.deprecated.ResourceIds;

@Getter
@AllArgsConstructor
// TODO replace display variables
public enum KillStreak {

    II(2, ResourceIds.Font.GAME_NOAH_KILLSTREAK_2_TITLE, ResourceIds.Font.GAME_NOAH_KILLSTREAK_2_BAR),
    III(3, ResourceIds.Font.GAME_NOAH_KILLSTREAK_3_TITLE, ResourceIds.Font.GAME_NOAH_KILLSTREAK_3_BAR),
    IV(4, ResourceIds.Font.GAME_NOAH_KILLSTREAK_4_TITLE, ResourceIds.Font.GAME_NOAH_KILLSTREAK_4_BAR),
    V(5, ResourceIds.Font.GAME_NOAH_KILLSTREAK_5_TITLE, ResourceIds.Font.GAME_NOAH_KILLSTREAK_5_BAR),
    VI(6, ResourceIds.Font.GAME_NOAH_KILLSTREAK_6_TITLE, ResourceIds.Font.GAME_NOAH_KILLSTREAK_6_BAR),
    VII(7, ResourceIds.Font.GAME_NOAH_KILLSTREAK_MORE_TITLE, ResourceIds.Font.GAME_NOAH_KILLSTREAK_MORE_BAR);

    private final int kills;
    private final String display;
    private final String barDisplay;

    public static KillStreak fromKills(int kills) {
        for (KillStreak streak : values()) {
            if (streak.kills == kills) return streak;
        }
        return null;
    }

}
