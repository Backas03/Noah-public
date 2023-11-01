package org.caramel.backas.noah.api.scoreboard;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.Nullable;

@Deprecated
public class ReverseSidebarScoreboard {

    public static ReverseSidebarScoreboard from(Scoreboard scoreboard) {
        return new ReverseSidebarScoreboard(scoreboard);
    }

    public static ReverseSidebarScoreboard newInstance() {
        return new ReverseSidebarScoreboard();
    }

    public static ReverseSidebarScoreboard newInstance(Component displayName) {
        return new ReverseSidebarScoreboard(displayName);
    }

    private final Scoreboard scoreboard;

    private ReverseSidebarScoreboard(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
        if (scoreboard.getObjective(DisplaySlot.SIDEBAR) == null) {
            initObjective(Component.empty());
        }
    }

    private ReverseSidebarScoreboard() {
        this(Component.empty());
    }

    private ReverseSidebarScoreboard(Component displayName) {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        initObjective(displayName);
    }

    private Objective initObjective(Component displayName) {
        Objective objective = this.scoreboard.registerNewObjective(
                "objective",
                Criteria.DUMMY,
                displayName
        );
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        return objective;
    }

    public @Nullable String getContent(int score) {
        Team team = scoreboard.getTeam(String.valueOf(score));
        if (team == null) return null;
        return ((TextComponent) team.prefix()).content();
    }

    public void setContent(int score, String content) {
        Objective objective = scoreboard.getObjective(DisplaySlot.SIDEBAR);
        if (objective == null) {
            objective = initObjective(Component.empty());
        }

        String prefix = getContent(score);
        Team team = scoreboard.getTeam(String.valueOf(score));
        if (content == null) {
            if (prefix != null) {
                objective.getScore(prefix).resetScore();
                scoreboard.getTeams().remove(team);
            }
            return;
        }
        if (prefix == null) {
            team = scoreboard.registerNewTeam(String.valueOf(score));
        } else {
            objective.getScore(prefix).resetScore();
            if (team == null) team = scoreboard.registerNewTeam(String.valueOf(score));
            else team.prefix(null);
        }

        team.prefix(Component.text(content));
        objective.getScore(content).setScore(score);
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }
}
