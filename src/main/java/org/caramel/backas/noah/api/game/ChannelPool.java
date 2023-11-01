package org.caramel.backas.noah.api.game;

import lombok.Getter;

import java.util.*;

public class ChannelPool {

    @Getter
    private final Set<GameChannel<?>> channels;
    private final AbstractGame game;

    public ChannelPool(AbstractGame game) {
        this.channels = new HashSet<>();
        this.game = game;
    }

    public ChannelPool(AbstractGame game, Collection<? extends GameChannel<?>> channels) {
        this.game = game;
        this.channels = new HashSet<>();
        this.channels.addAll(channels);
    }

    public void addGameChannel(GameChannel<?> channel) {
        channels.add(channel);
    }

    public void removeGameChannel(GameChannel<?> channel) {
        channels.remove(channel);
    }

    public ChannelPool emptyChannelPool() {
        Set<GameChannel<?>> channels = new HashSet<>();
        for (GameChannel<?> channel : this.channels) {
            if (channel.isEmpty()) channels.add(channel);
        }
        return new ChannelPool(game, channels);
    }

    public GameChannel<?> getRandomly() {
        if (channels.size() == 0) return null;
        List<GameChannel<?>> channelList = new ArrayList<>(channels);
        return channelList.get((int) (Math.random() * (channelList.size())));
    }

    public GameChannel<?> getByChannelName(String name) {
        for (GameChannel<?> channel : this.channels) {
            if (channel.getName().equals(name)) return channel;
        }
        return null;
    }

    public GameChannel<?> getByMapName(String name) {
        for (GameChannel<?> channel : this.channels) {
            if (channel.getGameMap().getName().equals(name)) return channel;
        }
        return null;
    }

    public boolean isEmpty() {
        return channels.isEmpty();
    }

}