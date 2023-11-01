package org.caramel.backas.noah.resource;

import kr.lostwar.fmj.api.FMJ;
import kr.lostwar.fmj.api.weapon.Weapon;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.caramel.backas.noah.deprecated.ResourceIds;
import org.caramel.backas.noah.game.deathmatch.DeathMatchGame;
import org.caramel.backas.noah.game.deathmatch.DeathMatchParticipant;
import org.caramel.backas.noah.game.deathmatch.KillStreak;
import org.caramel.backas.noah.game.personaldeathmatch.PDMGame;
import org.caramel.backas.noah.game.teamdeathmatch.TDMGame;
import org.caramel.backas.noah.util.ComponentUtil;
import org.caramel.backas.noah.util.ItemStackUtil;
import java.util.Arrays;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;

public class DeathMatchResource {

    public static ItemStack getPDMIcon(PDMGame game) {
        return ItemStackUtil.get(Material.YELLOW_BANNER, "<green>" + game.getName(), Arrays.asList("<gray>클릭 시 게임 매칭 대기열에 입장합니다", "<red>(파티장을 통하여 입장하실 수 있습니다)"));
    }

    public static ItemStack getTDMIcon(TDMGame game) {
        return ItemStackUtil.get(Material.WHITE_BANNER, "<green>" + game.getName(), Arrays.asList("<gray>클릭 시 게임 매칭 대기열에 입장합니다", "<red>(파티장을 통하여 입장하실 수 있습니다)"));
    }

    public static String getKillLogMessage(DeathMatchGame game, DeathMatchParticipant attacker, KillStreak killStreak, DeathMatchParticipant victim, String weaponName) {
        StringBuilder stringBuilder = new StringBuilder();

        String sKillStreak = killStreak != null ? " " + killStreak.getBarDisplay() : "";

        String msg = game.getConfig().getKillLogFormat().get()
                .replaceAll("%attacker%", "<" + attacker.getTeam().getColor() + ">" + attacker.getUser().getName())
                .replaceAll("%kill_streak%", "<reset>" + sKillStreak)
                .replaceAll("%victim%", "<" + victim.getTeam().getColor() + ">" + victim.getUser().getName())
                .replaceAll("%weapon%", weaponName);

        String stripMsg = ChatColor.stripColor(msg);
        stringBuilder.append(" ".repeat(Math.max(0, (105 - stripMsg.length() - (stripMsg.replaceAll("[^ㄱ-힣]", "").length()
                + (stripMsg.contains("▄") ? 4 : 0) + (stripMsg.contains("▬▬") ? 2 : 0))))));
        stringBuilder.append(msg);
        return stringBuilder.toString();
    }

    public static String getKillStreakTitleFormat(KillStreak killStreak) {
        return killStreak.getDisplay();
    }

    public static String getKillStreakSubTitleFormat(KillStreak killStreak) {
        return killStreak.getDisplay();
    }

    public static Inventory createGunInventory(DeathMatchGame game) {
        Inventory gunInventory = Bukkit.createInventory(null, (int) Math.min(game.getConfig().getGunInventoryRows().get() * 9, Math.ceil((double) game.getConfig().getWeaponKeys().get().size() / 9)) * 9, "클릭하여 총을 선택합니다");
        for (String key : game.getConfig().getWeaponKeys().get()) {
            Weapon w = FMJ.findWeapon(key);
            if (w != null) gunInventory.addItem(w.getItemStack());
        }
        return gunInventory;
    }


    public static ItemStack getHealKitItem() {
        return HEAL_KIT;
    }

    private static final ItemStack HEAL_KIT = ItemStackUtil.get(Material.IRON_PICKAXE,
        text("힐 킷", ResourceIds.Styles.RED_NO_ITALIC),
        ComponentUtil.create(
            Component.keybind("key.attack", ResourceIds.Styles.WHITE_NO_ITALIC),
            text("시 ", WHITE), text("체력", RED), text("을 회복합니다", GRAY)
        ), meta -> { meta.setCustomModelData(50001); } // ...
    );
}
