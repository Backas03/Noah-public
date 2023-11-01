package org.caramel.backas.noah.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

public final class ColorString {

    private ColorString() { throw new UnsupportedOperationException(); }


    /**
     * 엠퍼센드(&) 레거시 문자열을 {@link Component}로 변환합니다.
     *
     * @param msg 레거시 문자열
     * @return 텍스트 컴포넌트
     */
    @NotNull
    public static Component parse(@NotNull String msg) {
        return MiniMessage.miniMessage().parse(msg);
    }
}
