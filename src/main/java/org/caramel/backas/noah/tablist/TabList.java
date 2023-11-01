package org.caramel.backas.noah.tablist;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.caramel.backas.noah.api.user.User;

public class TabList {


    public static void updateStatus(Player target) {
        Scoreboard scoreboard = getScoreBoard();
        UserStatus targetStatus = UserStatus.getStatus(target);
        updateName(target);

        if (targetStatus == UserStatus.IN_GAME) {
            Team team = scoreboard.getEntryTeam(target.getName());
            if (team != null) {
                team.removeEntry(target.getName());
            }
            return;
        }

        for (UserStatus status : UserStatus.values()) {
            if (status == UserStatus.IN_GAME) {
                continue;
            }
            Team team = getTeam(scoreboard, status);
            if (team == null) {
                team = initTeam(scoreboard, status);
            }
            if (status == targetStatus) {
                team.addEntry(target.getName());
            }
        }

    }

    public static void apply(Player player) {
        player.setScoreboard(getScoreBoard());
    }

    private static Scoreboard scoreboard;

    public static Scoreboard getScoreBoard() {
        if (scoreboard == null) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        }
        return scoreboard;
    }

    public static Team getTeam(Scoreboard scoreboard, UserStatus status) {
        return scoreboard.getTeam(status.name());
    }

    public static Team initTeam(Scoreboard scoreboard, UserStatus status) {
        scoreboard.registerNewTeam(status.name());
        return scoreboard.getTeam(status.name());
    }

    private static Component createStatus(Player player) {
        User user = User.get(player);
        TextComponent.Builder builder = Component.text();
        if (user.isInGame()) {
            builder.content("(InGame)")
                    .color(TextColor.color(255, 178, 200));
        } else if (user.isInQueue()) {
            builder.content("(InQueue)")
                    .color(TextColor.color(255, 187, 0));
        } else if (user.getAfkCache().isAFK()){
            builder.content("(AFK)")
                    .color(TextColor.color(0, 130, 153));
        } else {
            builder.content("(Online)")
                    .color(TextColor.color(183, 240, 177));
        }
        builder.append(Component.text()
                .content(" " + player.getName())
                .color(NamedTextColor.WHITE)
        );
        return builder.build();
    }

    private static void updateName(Player player) {
        /* only players in the lobby can be updated */
        player.playerListName(createStatus(player));
    }

}
