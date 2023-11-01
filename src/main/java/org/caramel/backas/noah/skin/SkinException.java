package org.caramel.backas.noah.skin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;

@AllArgsConstructor
public class SkinException extends Exception {

    @Getter
    private final Component component;

}
