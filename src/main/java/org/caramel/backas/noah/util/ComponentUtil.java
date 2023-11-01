package org.caramel.backas.noah.util;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class ComponentUtil {

    private ComponentUtil() { throw new UnsupportedOperationException(); }


    /**
     * 왜 효율적인데 이렇게 더럽게 만들어놨을까
     * <br>
     * 아이템에서 만큼은 이것이 더 효율적임
     * <br>
     * 엄청난 시간 복잡도! 신난다!
     *
     * @param main 메인 컴포넌트
     * @param others 추가 컴포넌트
     * @return 텍스트 컴포넌트
     */
    @NotNull
    public static Component create(@NotNull Component main, Component... others) {
        for (final Component other : others) main = main.append(other);
        return main;
    }
}
