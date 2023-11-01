package org.caramel.backas.noah.game.teamdeathmatch;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.caramel.backas.noah.api.game.*;
import org.caramel.backas.noah.api.game.rating.EloRatingCalculator;
import org.caramel.backas.noah.api.game.rating.Rating;
import org.caramel.backas.noah.api.party.Party;
import org.caramel.backas.noah.api.tier.Tier;
import org.caramel.backas.noah.api.user.User;
import org.caramel.backas.noah.game.deathmatch.*;

import java.util.*;

public class TDMChannel extends DeathMatchChannel<DeathMatchGameMap> {

    public TDMChannel(TDMGame game, DeathMatchGameMap gameMap) {
        super(game, gameMap);
    }

    protected void giveNoahPoint() {

        for (DeathMatchParticipant participant : getParticipants().values()) {

            int kills = participant.getKills();
            int assists = participant.getAssists();

            int point = kills + assists / 2;

            User user = participant.getUser();
            user.getNoahPoint().add(point);

            user.sendMessage(Component.text()
                    .content(String.valueOf(point))
                    .color(TextColor.color(255, 167, 167))
                    .append(Component.text()
                            .content(" Noah-Point")
                            .color(TextColor.color(209, 178, 255)))
                    .append(Component.text()
                            .content(" 를 획득하셨습니다.")
                            .color(NamedTextColor.WHITE))
                    .build()
            );

        }


    }

    protected void calculateMMR() {

        DeathMatchTeamType winnerTeam = null;
        int killRed = getKills(DeathMatchTeamType.RED);
        int killBlack = getKills(DeathMatchTeamType.BLACK);
        if (killBlack > killRed) {
            winnerTeam = DeathMatchTeamType.BLACK;
        } else if (killBlack < killRed) {
            winnerTeam = DeathMatchTeamType.RED;
        }

        double q = 0d;
        for (IParticipant participant : getParticipants().values()) {
            if (!participant.getUser().isLoad()) continue;
            int rating = participant.getUser().getRating(TDMGame.class).get();
            q += Math.pow(10d, (double) rating / 400);
        }

        if (winnerTeam != null) {
            sendOutcomeSound(getParticipants((winnerTeam)), "tdm.victory");
            sendOutcomeSound(getParticipants((winnerTeam.getSide())), "tdm.lose");
        }

        for (DeathMatchParticipant participant : getParticipants().values()) {
            double score;
            QuitData data = getQuitLocation().get(participant.getUniqueId());
            if (data != null && data.getTime() >= 240) score = 0d; // breakout penalty
            else if (participant.getUser().getAfkCache().isAFK()) score = 0d; // afk penalty
            else if (winnerTeam == null) score = 0.5;
            else if (participant.getTeam() == winnerTeam) score = 1d;
            else score = 0d;

            int before = participant.getUser().getRating(TDMGame.class).get();
            int scoreDiff = EloRatingCalculator.calculate(TDMGame.class, participant, score, q);
            int after = before + scoreDiff;

            StringBuilder sb = new StringBuilder("<green>Rating <gray>| <yellow>팀 데스매치 <white>").append(before).append(" ");
            if (scoreDiff > 0) sb.append("<green>== +").append(scoreDiff).append(" ==> <white>").append(after);
            else if (scoreDiff == 0) sb.append("<gray>== 0 ==> <white>").append(after);
            else sb.append("<red>== ").append(scoreDiff).append(" ==> <white>").append(after);

            participant.getUser().sendMessage(sb.toString());
            Tier beforeTier = Tier.getTierByPoint(before);
            Tier afterTier = Tier.getTierByPoint(after);
            if (beforeTier != afterTier) {
                if (after > before) {
                    Player player = participant.getUser().toPlayer();
                    if (player != null) player.playSound(player.getLocation(), "tdm.tier.up", 1, 1);
                    participant.getUser().sendMessage("<green>Rating <gray>| <yellow>팀 데스매치 <gray>" + beforeTier.getName() + " <light_purple>티어에서 <white>" + afterTier.getName() + " <light_purple>티어로 승급하셨습니다");
                } else {
                    participant.getUser().sendMessage("<green>Rating <gray>| <yellow>팀 데스매치 <gray>" + beforeTier.getName() + " <light_purple>티어에서 <white>" + afterTier.getName() + " <light_purple>티어로 하락하셨습니다");
                }
            }
        }
    }

    private void sendOutcomeSound(Collection<? extends DeathMatchParticipant> participants, String sound) {
        for (DeathMatchParticipant participant : participants) {
            Player player = participant.getUser().toPlayer();
            if (player != null) player.playSound(player.getLocation(), sound, 1, 1);
        }
    }

    @Override
    protected void onOver(AbstractGame game, Map<Party, IGameTeam> players) {
        calculateMMR();
        giveNoahPoint();
        super.onOver(game, players);
    }

}
