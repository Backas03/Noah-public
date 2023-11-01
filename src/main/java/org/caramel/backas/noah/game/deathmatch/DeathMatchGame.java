package org.caramel.backas.noah.game.deathmatch;

import org.caramel.backas.noah.api.game.AbstractGame;
import org.caramel.backas.noah.api.game.ConfigContainer;

public abstract class DeathMatchGame extends AbstractGame implements ConfigContainer {

    @Override
    public abstract DeathMatchConfig getConfig();

}
