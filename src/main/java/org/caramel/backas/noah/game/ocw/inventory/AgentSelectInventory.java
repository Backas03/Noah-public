package org.caramel.backas.noah.game.ocw.inventory;

import moe.caramel.acacia.api.inventory.page.model.ArrayedPageInventory;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.caramel.backas.noah.Lobby;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.game.ocw.OCWParticipant;
import org.caramel.backas.noah.game.ocw.agent.OCWAgent;
import org.caramel.backas.noah.game.ocw.agent.impl.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AgentSelectInventory extends ArrayedPageInventory {

    public static void open(OCWParticipant participant) {
        participant.getUser().getPlayer().ifPresent(player -> {
            Set<OCWAgent> agents = Set.of(
                    new OCWAgentHide(),
                    new OCWAgentJosepin(),
                    new OCWAgentPrigue(),
                    new OCWAgentTirr(),
                    new OCWAgentViera(),
                    new OCWAgentViper(),
                    new OCWAgentVolk(),
                    new OCWAgentZenicx()
            );
            int size = (int) Math.ceil(agents.size() / 9d) * 9;
            List<AgentSelectInventoryContent> contents = new ArrayList<>();
            for (OCWAgent agent : agents) contents.add(new AgentSelectInventoryContent(participant, agent));
            AgentSelectInventory inv = new AgentSelectInventory(size, participant, contents);
            player.openInventory(inv.inventory);
        });
    }

    private final OCWParticipant participant;

    private AgentSelectInventory(int size, OCWParticipant participant, @NotNull List<AgentSelectInventoryContent> contents) {
        super(size, size, contents);
        this.participant = participant;
    }

    @Override
    protected @Nullable Component createTitle() {
        if (participant.getAgent() == null) return Component.text("클릭하여 요원을 선택합니다.");
        return Component.text("다음 요원: ").append(participant.getAgent().getInfo().getName());
    }

    @Override
    protected void onClick(@NotNull InventoryClickEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void process(@NotNull InventoryCloseEvent event) {
        if (event.getReason() != InventoryCloseEvent.Reason.PLUGIN) {
            if (!(event.getPlayer() instanceof Player player)) return;
            Bukkit.getScheduler().runTaskLater(Noah.getInstance(), () -> {
                if (Lobby.getRegion().isInside(player)) return;
                if (!participant.isRespawning()) return;
                player.openInventory(inventory);
            }, 1L);
        }
    }
}
