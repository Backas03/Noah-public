package org.caramel.backas.noah.rating;

import moe.caramel.daydream.util.Pair;
import org.caramel.backas.noah.user.User;
import org.jetbrains.annotations.NotNull;

public final class TDMEloUtil {

    /**
     * 유저 데이터를 로딩해야 하므로 비동기에서 계산합니다.
     * @param me 계산할 대상
     * @param opAvgMMR 대상의 적팀 평균 MMR
     * @param score 승리 1, 무승부 0.5, 패배 0
     * @param k 가중치
     * @return 계산 후 대상의 MMR 변경 값 (key = 이전, value = 변동 치)
     */
    public static @NotNull Pair<@NotNull Integer, @NotNull Integer> calculate(User me, int opAvgMMR, float score, float k) {
        TDMEloRating model = me.getDataContainer().getOrLoad(TDMEloRating.class);

        int before = model.rating;
        double expected = 1 / ((Math.pow(10.0, (double) (model.rating - opAvgMMR) / 400)) + 1);

        int newRating = (int) Math.round(model.rating + k * (score - expected));
        model.rating = newRating;

        return new Pair<>(before, newRating - before); // diff
    }

    private TDMEloUtil() {
        throw new UnsupportedOperationException();
    }
}
