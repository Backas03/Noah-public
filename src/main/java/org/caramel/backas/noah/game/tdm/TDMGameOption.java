package org.caramel.backas.noah.game.tdm;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.caramel.backas.noah.util.ComponentUtil;
import org.caramel.backas.noah.util.ItemStackUtil;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;

public final class TDMGameOption {

    public static final String SUB_WEAPON_KEY = "flatline";
    public static final String NAME = "팀 데스매치";

    public static final int GAME_TIME = 420;
    public static final int MIN_PLAYERS_TO_START = 2;
    public static final int MIN_JOIN_TIME = 10;
    public static final int MAX_KILL_STREAK = 7;
    public static final int JOIN_TIME = 5;
    public static final int RESPAWN_TIME = 5;
    public static final int GOD_MODE_DURATION = 4;
    public static final int GAME_BAR_POINT_TIME = 30;
    public static final int ASSIST_DAMAGE_THRESHOLD = 25;
    public static final int GUN_INVENTORY_ROWS = 1;

    public static final String[] WEAPON_KEYS = { "Reaper", "ghost", "awper", "scout" };

    public static final TextColor RED_TEAM_COLOR = TextColor.color(255, 133, 122);
    public static final TextColor BLACK_TEAM_COLOR = NamedTextColor.DARK_GRAY;

    private static final ItemStack HEAL_KIT = ItemStackUtil.get(Material.IRON_PICKAXE,
            text("힐 킷", RED),
            ComponentUtil.create(
                    Component.keybind("key.attack", WHITE),
                    text("시 ", WHITE), text("체력", RED), text("을 회복합니다", GRAY)
            ), meta -> { meta.setCustomModelData(50001); } // ...
    );

    public static ItemStack getHealKit() {
        return HEAL_KIT;
    }

    private TDMGameOption() {
        throw new UnsupportedOperationException();
    }
}
