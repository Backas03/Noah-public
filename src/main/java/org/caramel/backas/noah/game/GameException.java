package org.caramel.backas.noah.game;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

public class GameException extends Exception {

    @Getter
    private final Component component;

    public GameException(String message) {
        super(message);
        component = Component.text(message);
    }

    public GameException(Component component) {
        super(((TextComponent) component).content());
        this.component = component;
    }
}
