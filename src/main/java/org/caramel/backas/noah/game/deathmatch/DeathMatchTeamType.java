package org.caramel.backas.noah.game.deathmatch;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.caramel.backas.noah.api.game.IGameTeam;

@Getter
@AllArgsConstructor
public enum DeathMatchTeamType implements IGameTeam {

    RED("레드 팀", "red", NamedTextColor.RED),
    BLACK("블랙 팀", "dark_gray", NamedTextColor.DARK_GRAY);

    private final String name;
    private final String color;
    private final TextColor textColor;

    public DeathMatchTeamType getSide() {
        return this == RED ? BLACK : RED;
    }

}
