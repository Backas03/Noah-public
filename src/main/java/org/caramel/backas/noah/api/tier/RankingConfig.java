package org.caramel.backas.noah.api.tier;


import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.api.config.Config;
import org.caramel.backas.noah.api.game.AbstractGame;
import org.caramel.backas.noah.api.game.rating.Rating;
import org.caramel.backas.noah.game.teamdeathmatch.TDMGame;

import java.io.File;
import java.util.*;

public class RankingConfig extends Config {

    private final Map<Class<? extends AbstractGame>, Integer> CASHING_SIZE;
    public static final int DEFAULT_CASHING_SIZE = 5;

    public RankingConfig() {
        super(new File(Noah.getInstance().getDataFolder(), "ranking.yml"));
        CASHING_SIZE = new HashMap<>();
        CASHING_SIZE.put(TDMGame.class, 5);
    }

    public int getCashingSize(Class<? extends AbstractGame> gameType) {
        return CASHING_SIZE.getOrDefault(gameType, DEFAULT_CASHING_SIZE);
    }

    public void updateRanking(UUID uniqueId, String name, Rating rating, Class<? extends AbstractGame> gameType) {
        List<Ranker> rankers = getRankers(gameType);
        boolean c = false;
        for (Ranker r : rankers) {
            if (r.getUniqueId().equals(uniqueId)) {
                r.setRating(rating.get());
                c = true;
                break;
            }
        }
        if (!c) rankers.add(new Ranker(uniqueId, name, rating.get()));
        rankers.sort((o1, o2) -> {
            if (o1.getRating() > o2.getRating()) {
                return -1;
            } else if (o1.getRating() < o2.getRating()) {
                return 1;
            }
            return 0;
        });
        for (int i=5; i<rankers.size(); i++) {
            rankers.remove(i);
        }
        get().set(gameType.getSimpleName(), rankers);
    }

    @SuppressWarnings("unchecked")
    public LinkedList<Ranker> getRankers(Class<? extends AbstractGame> gameType) {
        List<?> list = get().getList(gameType.getSimpleName());
        if (list == null) return new LinkedList<>();
        List<Ranker> rankers = (List<Ranker>) list;
        rankers.sort((o1, o2) -> {
            if (o1.getRating() > o2.getRating()) {
                return -1;
            } else if (o1.getRating() < o2.getRating()) {
                return 1;
            }
            return 0;
        });
        return new LinkedList<>(rankers);
    }


}
