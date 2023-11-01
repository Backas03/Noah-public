package org.caramel.backas.noah.prefix.gui.content;

import moe.caramel.acacia.api.inventory.page.Content;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.prefix.PrefixData;
import org.caramel.backas.noah.user.User;

public class PrefixContent extends Content {

    public final String namespacedKey;

    public PrefixContent(ItemStack icon, String namespacedKey) {
        super(icon, false, (event, inventory, clicked) -> {
            if (!(event.getWhoClicked() instanceof Player player)) return;
            if (!(clicked instanceof PrefixContent content)) return;
            User.get(player).getDataContainer().getOrLoadAsync(PrefixData.class).thenApply(prefixData -> {
                if (content.namespacedKey.equals(prefixData.getCurrentNamespacedKey())) return prefixData;
                prefixData.setPrefix(namespacedKey);
                Bukkit.getScheduler().runTask(Noah.getInstance(), () -> {
                    player.closeInventory();
                    player.sendMessage(Component.text().append(
                            Component.text("[칭호] ", NamedTextColor.GREEN),
                            Component.text("칭호 변경이 완료 되었습니다.", NamedTextColor.WHITE)
                    ));
                });
                return prefixData;
            });
        });
        this.namespacedKey = namespacedKey;
    }


}
