package org.caramel.backas.noah.tablist;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.api.event.game.GameDequeueEvent;
import org.caramel.backas.noah.api.event.game.GameEnqueueEvent;
import org.caramel.backas.noah.api.event.game.GameOverEvent;
import org.caramel.backas.noah.api.event.game.GameStartEvent;
import org.caramel.backas.noah.api.event.user.afk.AFKEndEvent;
import org.caramel.backas.noah.api.event.user.afk.AFKStartEvent;
import org.caramel.backas.noah.api.party.Party;
import org.caramel.backas.noah.api.user.User;

public class TabListListener implements Listener {

    @EventHandler
    public void onGameStart(GameStartEvent e) {
        for (Party party : e.getChannel().getPlayers().keySet()) {
            for (User user : party.getAllMembers()) {
                Player player = user.toPlayer();
                if (player == null) continue;
                TabList.updateStatus(player);
            }
        }
    }

    @EventHandler
    public void onEnqueue(GameEnqueueEvent e) {
        for (User user : e.getParty().getAllMembers()) {
            Player player = user.toPlayer();
            if (player == null) continue;
            TabList.updateStatus(player);
        }
    }

    @EventHandler
    public void onDequeue(GameDequeueEvent e) {
        for (User user : e.getParty().getAllMembers()) {
            Player player = user.toPlayer();
            if (player == null) continue;
            TabList.updateStatus(player);
        }
    }

    @EventHandler
    public void onGameOver(GameOverEvent e) {
        for (Party party : e.getChannel().getPlayers().keySet()) {
            for (User user : party.getAllMembers()) {
                Player player = user.toPlayer();
                if (player == null) continue;
                TabList.updateStatus(player);
                TabList.apply(player);
            }
        }
    }

    @EventHandler
    public void onAFKStart(AFKStartEvent e) {
        TabList.updateStatus(e.getUser().toPlayer());
    }

    @EventHandler
    public void onAFKEnd(AFKEndEvent e) {
        TabList.updateStatus(e.getUser().toPlayer());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        TabList.updateStatus(e.getPlayer());
        TabList.apply(e.getPlayer());
    }

}
