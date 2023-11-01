package org.caramel.backas.noah.game;

import moe.caramel.daydream.sidebar.EntryBuilder;
import moe.caramel.daydream.sidebar.SidebarDecorator;
import moe.caramel.daydream.sidebar.SidebarProcessor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.prefix.Prefix;
import org.caramel.backas.noah.util.TimeUtil;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.*;
import static org.caramel.backas.noah.util.ComponentUtil.fusion;

public class ScoreboardManager {

    public static void init() {
        scoreboard = Bukkit.getServer().getScoreboardManager().getMainScoreboard();
        Objective sidebar = scoreboard.getObjective("0");
        if (sidebar == null) sidebar = scoreboard.registerNewObjective("0", Criteria.DUMMY, Component.empty());
        sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
        sidebar.disableIndividualSidebar(); // 혹시라도 활성화 되어있는 경우

        // 개인 사이드바 활성화
        processor = sidebar.enableIndividualSidebar(new Handler());
    }

    // =========================================================

    private static Scoreboard scoreboard;
    private static SidebarProcessor processor;

    static final class Handler implements SidebarDecorator {

        static final Component ENTRY_SEPARATOR = text("                   ", GOLD, BOLD, STRIKETHROUGH);
        static final Component ENTRY_PART_PREFIX = text("   >> ", GRAY, BOLD);
        static final Component ENTRY_GAME_INFO = text(" 게임 정보", GREEN, BOLD);
        static final Component ENTRY_JOIN_COUNT = text(" 참여 인원", AQUA, BOLD);
        static final Component ENTRY_VOTE_STATUS = text(" 투표 상태", YELLOW, BOLD);

        @Override
        public Component getTitle(@NotNull Player player) {
            return Prefix.NOAH;
        }

        @Override
        public EntryBuilder getEntries(@NotNull Player player) {
            final EntryBuilder builder = EntryBuilder.builder();

            // 플레이어가 인게임인 경우 빈 사이드바
            if (!player.getWorld().getName().equals("lobby") && !player.getWorld().getName().equals("range")) {
                return builder;
            }

            // 로비나 훈련장인 경우
            final GameManager manager = GameManager.getInstance(); // 심한 오버헤드를 방지하기 위해 캐시
            final Component state;
            if (manager.isGameStarted()) {
                state = fusion(
                    text("게임 중 ", RED),
                    text("(" + TimeUtil.formatTime(manager.getCurrentGame().getTime()) + ")", GRAY)
                );
            } else {
                state = fusion(
                    text("시작 대기 중 ", GOLD),
                    text("(" + TimeUtil.formatTime(manager.getTime()) + ")", GRAY)
                );
            }

            builder
                   // 구분선
                   .next(ENTRY_SEPARATOR)
                   // 게임 정보
                   .next(ENTRY_GAME_INFO)
                   .next(fusion(ENTRY_PART_PREFIX, text(manager.getGameLauncher().getName(), YELLOW)))
                   .next(fusion(ENTRY_PART_PREFIX, manager.getMap().getName().color(WHITE)))
                   .next(fusion(text(" ┗ ", GRAY, BOLD), state))
                   .blank()
                   // 참여 인원
                   .next(ENTRY_JOIN_COUNT)
                   .next(fusion(ENTRY_PART_PREFIX, text(manager.getJoined() + "명", WHITE)))
                   .blank()
                   // 투표 상태
                   .next(ENTRY_VOTE_STATUS)
                   .next(fusion(ENTRY_PART_PREFIX, text(manager.getVotes() + " / " + manager.getVotesToStart(), GRAY)))
                    // 구분선
                   .next(ENTRY_SEPARATOR);

            return builder;
        }
    }

    public static void activateScoreboard(Player player) {
        player.setScoreboard(scoreboard);
        processor.activate(Noah.getInstance(), player, 20L);
    }
}
