package org.caramel.backas.noah.advancement;

import lombok.Getter;
import org.bukkit.advancement.Advancement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public final class AdvancementConstant {

    private final @NotNull Advancement advancement;
    private final @Nullable AdvancementAward award;

    public AdvancementConstant(@NotNull Advancement advancement, @Nullable AdvancementAward award) {
        this.advancement = advancement;
        this.award = award;
    }
}
