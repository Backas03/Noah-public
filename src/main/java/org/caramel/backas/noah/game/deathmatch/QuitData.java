package org.caramel.backas.noah.game.deathmatch;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@Getter
@Setter
@AllArgsConstructor
public class QuitData {

    private Location location;
    private int time;
    private int quitTime;

}
