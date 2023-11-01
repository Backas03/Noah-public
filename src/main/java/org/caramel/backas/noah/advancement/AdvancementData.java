package org.caramel.backas.noah.advancement;

import moe.caramel.daydream.advancement.AdvancementBuilder;
import java.util.function.Consumer;

public interface AdvancementData {

    String getKey();

    Consumer<AdvancementBuilder> getBuilder();

    AdvancementAward getAward();

    default void parent(AdvancementBuilder builder, AdvancementConstant constant) {
        builder.parent(constant.getAdvancement());
    }
}
