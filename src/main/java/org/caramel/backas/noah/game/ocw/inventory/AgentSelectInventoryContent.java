package org.caramel.backas.noah.game.ocw.inventory;

import moe.caramel.acacia.api.inventory.page.Content;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.caramel.backas.noah.game.ocw.GameOCW;
import org.caramel.backas.noah.game.ocw.OCWParticipant;
import org.caramel.backas.noah.game.ocw.agent.OCWAgent;

public class AgentSelectInventoryContent extends Content {

    public AgentSelectInventoryContent(OCWParticipant participant, OCWAgent agent) {
        super(agent.getIcon(), false, (event, inventory, clicked) -> {
            if (!(event.getWhoClicked() instanceof Player player)) return;

            if (participant.getAgent() == null) { // 처음 요원을 선택하는 창일 때
                participant.setAgent(agent);
                GameOCW.initAgent(participant);
                player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                return;
            }
            participant.setAgent(agent);
            event.getView().sendTitleUpdate(Component.text("다음 요원: ").append(agent.getName()));
        });
    }
}
