package org.caramel.backas.noah.game.ocw;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.caramel.backas.noah.game.ITeamType;

@AllArgsConstructor
@Getter
public enum OCWTeamType implements ITeamType {

    BLUE(NamedTextColor.AQUA, BossBar.Color.BLUE),
    PINK(TextColor.fromHexString("#F88AAE"), BossBar.Color.PINK);

    private final TextColor color;
    private final BossBar.Color barColor;

}
