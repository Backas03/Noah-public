package org.caramel.backas.noah.api.tier;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.caramel.backas.noah.api.game.rating.Rating;
import org.caramel.backas.noah.deprecated.ResourceIds;
import org.jetbrains.annotations.NotNull;
import java.text.DecimalFormat;

@AllArgsConstructor
@Getter
public enum Tier {

    STONE("스톤", 0),
    BRONZE("브론즈", 500),
    SILVER("실버", 1000),
    GOLD("골드", 1200),
    DIAMOND("다이아몬드", 1400),
    RUBY("루비", 1700),
    KING("킹", 2050),
    THE_KING("더 킹", 2500);

    private final String name;
    @Getter
    private final int startPoint;

    public Tier getNextTier() {
        return Tier.values()[Math.min(getIndex()+1, Tier.values().length-1)];
    }

    public Tier getPreviousTier() {
        return Tier.values()[Math.max(0, getIndex()-1)];
    }

    public static Tier getTierByPoint(int point) {
        for (int i=0; i<values().length-1; i++) {
            if (point >= THE_KING.getStartPoint()) return THE_KING;
            Tier tier = values()[i];
            if (point >= tier.getStartPoint() && point < tier.getNextTier().getStartPoint()) return tier;
        }
        return STONE;
    }

    public int getRating(int point) {
        int start = point - startPoint;
        for (int i=0; i<5; i++) {
            if (start >= i * 100 && start < (i + 1) * 100) return i + 1;
        }
        return 1;
    }

    public int getIndex() {
        for (int i=0; i<Tier.values().length; i++) {
            if (this == Tier.values()[i]) return i;
        }
        return 0;
    }

    public static Tier fromName(String name) {
        for (Tier tier : values()) {
            if (tier.getName().equals(name)) return tier;
        }
        return STONE;
    }

    public static Tier fromIndex(int index) {
        return Tier.values()[Math.max(0, Math.min(index, Tier.values().length-1))];
    }

    /* ================== *
     *    ResourcePack    *
     * ================== */

    /**
     * POSITION: ASCII / -18
     */
    private static final Style ASC_M18 = Style.empty().font(Key.key("asc_m18"));

    /**
     * FORMAT: 20,020,912
     */
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,###");

    /**
     * 레이팅 보스바의 정보를 변경합니다.
     * <br>
     * 리소스팩 특성상 레이팅 보스바 위에 더미 보스바가 하나 더 있어야합니다.
     * (임의 변경을 금지합니다.)
     *
     * @param bossBar BossBar Object
     * @param displayWidth (개인 설정) 플레이어의 디스플레이 해상도에 맞는 {@link ResourceIds}
     *                     <br> 일반적으로 SPACE_HALF_870, 1010, 1400, 2900를 사용합니다.
     * @param rating player's glicko rating
     *
     * @deprecated Acacia(구 caramelLibraryLegacy)와 동기화 되어야합니다.
     * <br>모드로 해당 기능을 이전할 계획 (...)
     */
    @Deprecated
    public static void status(@NotNull BossBar bossBar, @NotNull String displayWidth, @NotNull Rating rating) {
        final double score = rating.get();
        String icon;
        if (score < 500.) icon = ResourceIds.Font.GAME_NOAH_RANK_1_BAR;
        else if (score < 1000.) icon = ResourceIds.Font.GAME_NOAH_RANK_2_BAR;
        else if (score < 1500.) icon = ResourceIds.Font.GAME_NOAH_RANK_3_BAR;
        else if (score < 2000.) icon = ResourceIds.Font.GAME_NOAH_RANK_4_BAR;
        else if (score < 2500.) icon = ResourceIds.Font.GAME_NOAH_RANK_5_BAR;
        else if (score < 3000.) icon = ResourceIds.Font.GAME_NOAH_RANK_6_BAR;
        else if (score < 3500.) icon = ResourceIds.Font.GAME_NOAH_RANK_7_BAR;
        else if (score < 4000.) icon = ResourceIds.Font.GAME_NOAH_RANK_8_BAR;
        else icon = ResourceIds.Font.GAME_NOAH_RANK_8_TOP_BAR;
        bossBar.name(Component.text( displayWidth + icon +
            ResourceIds.Font.SPACE_BACK_9.repeat((score < 1000) ? ( (score < 600) ? 2 : 3 ) : 4) // wt* is this?
        ).append(Component.text(DECIMAL_FORMAT.format(score) + "RP", ASC_M18)));
    }
}
