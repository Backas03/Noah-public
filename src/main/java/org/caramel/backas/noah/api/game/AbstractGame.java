package org.caramel.backas.noah.api.game;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.caramel.backas.noah.api.game.matching.MatchingPool;

@Getter
public abstract class AbstractGame {

    public abstract String getName();
    public abstract ItemStack getIcon();

    protected abstract MatchingPool newMatchingPool();

    private final MatchingPool matchingPool;
    private final ChannelPool channelPool;


    public AbstractGame() {
        this.matchingPool = newMatchingPool();
        this.channelPool = new ChannelPool(this);
    }

    public Component name() {
        return Component.text(this.getName());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AbstractGame compare && compare.getName().equals(getName());
    }

    @Override
    public String toString() {
        return getName();
    }


}
