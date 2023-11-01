package org.caramel.backas.noah.api.game.rating;

import org.caramel.backas.noah.api.game.AbstractGame;
import org.caramel.backas.noah.api.game.IGameTeam;
import org.caramel.backas.noah.api.game.IParticipant;
import org.caramel.backas.noah.api.party.Party;
import org.caramel.backas.noah.api.user.User;
import org.caramel.backas.noah.game.deathmatch.DeathMatchParticipant;

import java.util.Collection;

public class EloRatingCalculator {

    public static int DEFAULT_RATING = 1000;

    public static int getAvgMMR(Party party, Class<? extends AbstractGame> gameType) {
        int avg = 0;
        Collection<User> all = party.getAllMembers();
        for (User user : all) {
            Rating rating = user.getRating(gameType);
            avg += rating.get();
        }
        return avg / all.size();
    }

    public static int calculate(Class<? extends AbstractGame> gameType, IParticipant me, double score, double q) {
        Rating rating = me.getUser().getRating(gameType);

        int before = rating.get();

        double expected = Math.pow(10.0, ((double) rating.get() / 400)) / q;

        int newRating = (int) Math.round(rating.get() + rating.getK() * (score - expected));

        rating.setRating(newRating);
        me.getUser().setRating(gameType, rating);

        return newRating - before;
    }

}
