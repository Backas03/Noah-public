package org.caramel.backas.noah.advancement;

import moe.caramel.daydream.advancement.AdvancementBuilder;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.advancement.impl.count.*;
import org.caramel.backas.noah.advancement.impl.kills.*;
import org.caramel.backas.noah.advancement.impl.killstreak.*;
import org.caramel.backas.noah.advancement.impl.mvp.assist.*;
import org.caramel.backas.noah.advancement.impl.mvp.kill.*;
import org.caramel.backas.noah.advancement.impl.win.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class AdvancementManager {

    public void init() {
        loadAdvancement(new AdvancementKillsRoot());
        loadAdvancement(new AdvancementKills100());
        loadAdvancement(new AdvancementKills500());
        loadAdvancement(new AdvancementKills1000());
        loadAdvancement(new AdvancementKills2000());

        loadAdvancement(new AdvancementWinRoot());
        loadAdvancement(new AdvancementWin20());
        loadAdvancement(new AdvancementWin50());
        loadAdvancement(new AdvancementWin100());
        loadAdvancement(new AdvancementWin250());

        loadAdvancement(new AdvancementKillMVPRoot());
        loadAdvancement(new AdvancementKillMVP5());
        loadAdvancement(new AdvancementKillMVP15());
        loadAdvancement(new AdvancementKillMVP30());
        loadAdvancement(new AdvancementKillMVP50());

        loadAdvancement(new AdvancementAssistMVPRoot());
        loadAdvancement(new AdvancementAssistMVP5());
        loadAdvancement(new AdvancementAssistMVP15());
        loadAdvancement(new AdvancementAssistMVP30());
        loadAdvancement(new AdvancementAssistMVP50());

        loadAdvancement(new AdvancementKillStreakRoot());
        loadAdvancement(new AdvancementKillStreak2_20());
        loadAdvancement(new AdvancementKillStreak3_30());
        loadAdvancement(new AdvancementKillStreak4_30());
        loadAdvancement(new AdvancementKillStreak5_30());

        loadAdvancement(new AdvancementCountRoot());
        loadAdvancement(new AdvancementCount5());
        loadAdvancement(new AdvancementCount10());
        loadAdvancement(new AdvancementCount15());
        loadAdvancement(new AdvancementCount20());

    }

    private final Map<String, AdvancementConstant> loaded = new HashMap<>();

    public void loadAdvancement(@NotNull String key, @NotNull Consumer<AdvancementBuilder> builder, @Nullable AdvancementAward award) {
        AdvancementBuilder createBuilder = Bukkit.createAdvancement(new NamespacedKey(Noah.getInstance(), key));
        builder.accept(createBuilder);
        Advancement advancement = Bukkit.getUnsafe().loadAdvancement(createBuilder);
        loaded.put(key, new AdvancementConstant(advancement, award));
    }

    public void loadAdvancement(AdvancementData data) {
        loadAdvancement(data.getKey(), data.getBuilder(), data.getAward());
    }

    public AdvancementConstant getConstant(String key) {
        return loaded.get(key);
    }
}
