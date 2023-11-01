package org.caramel.backas.noah.deprecated;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.function.Function;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

/**
 * CounterOnline 리소스팩 Id 상수 모음
 * (임의 변경을 금지합니다.)
 *
 * @deprecated Acacia(구 caramelLibraryLegacy)와 동기화 되어야합니다.
 */
@Deprecated
public final class ResourceIds {

    private ResourceIds() { throw new UnsupportedOperationException(); }

    public static class Styles {

        private Styles() { throw new UnsupportedOperationException(); }

        public static final Function<TextColor, Style> NO_ITALIC = (color) -> Style.style(color, TextDecoration.ITALIC.as(false));
        public static final Style BLACK_NO_ITALIC = Style.style(BLACK, TextDecoration.ITALIC.as(false));
        public static final Style DARK_BLUE_NO_ITALIC = Style.style(DARK_BLUE, TextDecoration.ITALIC.as(false));
        public static final Style DARK_GREEN_NO_ITALIC = Style.style(DARK_GREEN, TextDecoration.ITALIC.as(false));
        public static final Style DARK_AQUA_NO_ITALIC = Style.style(DARK_AQUA, TextDecoration.ITALIC.as(false));
        public static final Style DARK_RED_NO_ITALIC = Style.style(DARK_RED, TextDecoration.ITALIC.as(false));
        public static final Style DARK_PURPLE_NO_ITALIC = Style.style(DARK_PURPLE, TextDecoration.ITALIC.as(false));
        public static final Style GOLD_NO_ITALIC = Style.style(GOLD, TextDecoration.ITALIC.as(false));
        public static final Style GRAY_NO_ITALIC = Style.style(GRAY, TextDecoration.ITALIC.as(false));
        public static final Style DARK_GRAY_NO_ITALIC = Style.style(DARK_GRAY, TextDecoration.ITALIC.as(false));
        public static final Style BLUE_NO_ITALIC = Style.style(BLUE, TextDecoration.ITALIC.as(false));
        public static final Style GREEN_NO_ITALIC = Style.style(GREEN, TextDecoration.ITALIC.as(false));
        public static final Style AQUA_NO_ITALIC = Style.style(AQUA, TextDecoration.ITALIC.as(false));
        public static final Style RED_NO_ITALIC = Style.style(RED, TextDecoration.ITALIC.as(false));
        public static final Style LIGHT_PURPLE_NO_ITALIC = Style.style(LIGHT_PURPLE, TextDecoration.ITALIC.as(false));
        public static final Style YELLOW_NO_ITALIC = Style.style(YELLOW, TextDecoration.ITALIC.as(false));
        public static final Style WHITE_NO_ITALIC = Style.style(WHITE, TextDecoration.ITALIC.as(false));
    }

    public static class Font {

        private Font() { throw new UnsupportedOperationException(); }

        public static final String RANGE_START = "\uE100";
        public static final String RANGE_END = "\uF8FF";

        /* Space */
        // Half Space
        public static final String SPACE_HALF_1 = "\uE100";
        public static final String SPACE_HALF_2 = "\uE101";
        public static final String SPACE_HALF_15 = "\uE110";
        public static final String SPACE_HALF_20 = "\uE111";

        public static final String SPACE_HALF_870 = "\uE1E0";
        public static final String SPACE_HALF_1010 = "\uE1E1";
        public static final String SPACE_HALF_1400 = "\uE1E2";
        public static final String SPACE_HALF_2900 = "\uE1E3";

        // Back Space
        public static final String SPACE_BACK_1 = "\uE200";
        public static final String SPACE_BACK_2 = "\uE201";
        public static final String SPACE_BACK_3 = "\uE202";
        public static final String SPACE_BACK_4 = "\uE203";
        public static final String SPACE_BACK_5 = "\uE204";
        public static final String SPACE_BACK_6 = "\uE205";
        public static final String SPACE_BACK_7 = "\uE206";
        public static final String SPACE_BACK_8 = "\uE207";
        public static final String SPACE_BACK_9 = "\uE208";
        public static final String SPACE_BACK_10 = "\uE209";


        /* Common Component */
        // Rank
        public static final String RANK_ADMIN = "\uE300";
        public static final String RANK_MOD = "\uE301";
        public static final String RANK_DEV = "\uE302";
        public static final String RANK_STAFF = "\uE303";

        // Discord Status
        public static final String DISCORD_ON = "\uE350";
        public static final String DISCORD_OFF = "\uE351";
        public static final String DISCORD_LEAVE = "\uE352";

        // Game Component
        public static final String GAME_BLACK_SCREEN = "\uE400";

        public static final String GAME_MOUSE_LEFT_CLICK = "\uE410";
        public static final String GAME_MOUSE_WHEEL_CLICK = "\uE414";
        public static final String GAME_MOUSE_RIGHT_CLICK = "\uE418";


        /* GUI Container */
        private static final String STR_GUI_CONTAINER_CHANNEL = "\uE500";
        public static final TextComponent GUI_CONTAINER_CHANNEL = text(SPACE_BACK_8 + STR_GUI_CONTAINER_CHANNEL, WHITE);

        private static final String STR_GUI_CONTAINER_ZOMBIE_INVENTORY = "\uE550";
        public static final TextComponent GUI_CONTAINER_ZOMBIE_INVENTORY = text(SPACE_BACK_8 + STR_GUI_CONTAINER_ZOMBIE_INVENTORY, WHITE);
        private static final String STR_GUI_CONTAINER_ZOMBIE_WEAPON_SELECT = "\uE551";
        public static final TextComponent GUI_CONTAINER_ZOMBIE_WEAPON_SELECT = text(SPACE_BACK_8 + STR_GUI_CONTAINER_ZOMBIE_WEAPON_SELECT, WHITE);
        private static final String STR_GUI_CONTAINER_ZOMBIE_PRESET = "\uE552";
        public static final TextComponent GUI_CONTAINER_ZOMBIE_PRESET = text(SPACE_BACK_8 + STR_GUI_CONTAINER_ZOMBIE_PRESET, WHITE);

        private static final String STR_GUI_CONTAINER_NOAH_GAMEMENU = "\uE580";
        public static final TextComponent GUI_CONTAINER_NOAH_GAMEMENU = text(SPACE_BACK_8 + STR_GUI_CONTAINER_NOAH_GAMEMENU, WHITE);
        private static final String STR_GUI_CONTAINER_NOAH_PARTYMENU = "\uE581";
        public static final TextComponent GUI_CONTAINER_NOAH_PARTYMENU = text(SPACE_BACK_8 + STR_GUI_CONTAINER_NOAH_PARTYMENU, WHITE);


        /* Game Component */
        // Zombie

        // Noah
        public static final String GAME_NOAH_KILLSTREAK_2_TITLE = "\uE800";
        public static final String GAME_NOAH_KILLSTREAK_3_TITLE = "\uE801";
        public static final String GAME_NOAH_KILLSTREAK_4_TITLE = "\uE802";
        public static final String GAME_NOAH_KILLSTREAK_5_TITLE = "\uE803";
        public static final String GAME_NOAH_KILLSTREAK_6_TITLE = "\uE804";
        public static final String GAME_NOAH_KILLSTREAK_MORE_TITLE = "\uE805";

        public static final String GAME_NOAH_KILLSTREAK_2_BAR = "\uE808";
        public static final String GAME_NOAH_KILLSTREAK_3_BAR = "\uE809";
        public static final String GAME_NOAH_KILLSTREAK_4_BAR = "\uE80A";
        public static final String GAME_NOAH_KILLSTREAK_5_BAR = "\uE80B";
        public static final String GAME_NOAH_KILLSTREAK_6_BAR = "\uE80C";
        public static final String GAME_NOAH_KILLSTREAK_MORE_BAR = "\uE80D";

        public static final String GAME_NOAH_RANK_1_CHAT = "\uE810";
        public static final String GAME_NOAH_RANK_2_CHAT = "\uE811";
        public static final String GAME_NOAH_RANK_3_CHAT = "\uE812";
        public static final String GAME_NOAH_RANK_4_CHAT = "\uE813";
        public static final String GAME_NOAH_RANK_5_CHAT = "\uE814";
        public static final String GAME_NOAH_RANK_6_CHAT = "\uE815";
        public static final String GAME_NOAH_RANK_7_CHAT = "\uE816";
        public static final String GAME_NOAH_RANK_8_CHAT = "\uE817";
        public static final String GAME_NOAH_RANK_8_TOP_CHAT = "\uE818";

        public static final String GAME_NOAH_RANK_1_TITLE = "\uE820";
        public static final String GAME_NOAH_RANK_2_TITLE = "\uE821";
        public static final String GAME_NOAH_RANK_3_TITLE = "\uE822";
        public static final String GAME_NOAH_RANK_4_TITLE = "\uE823";
        public static final String GAME_NOAH_RANK_5_TITLE = "\uE824";
        public static final String GAME_NOAH_RANK_6_TITLE = "\uE825";
        public static final String GAME_NOAH_RANK_7_TITLE = "\uE826";
        public static final String GAME_NOAH_RANK_8_TITLE = "\uE827";
        public static final String GAME_NOAH_RANK_8_TOP_TITLE = "\uE828";

        public static final String GAME_NOAH_RANK_1_BAR = "\uE830";
        public static final String GAME_NOAH_RANK_2_BAR = "\uE831";
        public static final String GAME_NOAH_RANK_3_BAR = "\uE832";
        public static final String GAME_NOAH_RANK_4_BAR = "\uE833";
        public static final String GAME_NOAH_RANK_5_BAR = "\uE834";
        public static final String GAME_NOAH_RANK_6_BAR = "\uE835";
        public static final String GAME_NOAH_RANK_7_BAR = "\uE836";
        public static final String GAME_NOAH_RANK_8_BAR = "\uE837";
        public static final String GAME_NOAH_RANK_8_TOP_BAR = "\uE838";

        public static final String GAME_ZOMBIE_ANIMATION_VICTORY_HUMAN_0 = "\uE700";
        public static final String GAME_ZOMBIE_ANIMATION_VICTORY_HUMAN_1 = "\uE701";
        public static final String GAME_ZOMBIE_ANIMATION_VICTORY_HUMAN_2 = "\uE702";
        public static final String GAME_ZOMBIE_ANIMATION_VICTORY_HUMAN_3 = "\uE703";
        public static final String GAME_ZOMBIE_ANIMATION_VICTORY_HUMAN_4 = "\uE704";
        public static final String GAME_ZOMBIE_ANIMATION_VICTORY_HUMAN_5 = "\uE705";
        public static final String GAME_ZOMBIE_ANIMATION_VICTORY_HUMAN_6 = "\uE706";
        public static final String GAME_ZOMBIE_ANIMATION_VICTORY_HUMAN_7 = "\uE707";
        public static final String GAME_ZOMBIE_ANIMATION_VICTORY_HUMAN_8 = "\uE708";
        public static final String GAME_ZOMBIE_ANIMATION_VICTORY_HUMAN_9 = "\uE709";
        public static final String GAME_ZOMBIE_ANIMATION_VICTORY_HUMAN_10 = "\uE70A";
        public static final String GAME_ZOMBIE_ANIMATION_VICTORY_HUMAN_11 = "\uE70B";
        public static final String GAME_ZOMBIE_ANIMATION_VICTORY_HUMAN_12 = "\uE70C";

        public static final String GAME_ZOMBIE_ANIMATION_VICTORY_ZOMBIE_0 = "\uE710";
        public static final String GAME_ZOMBIE_ANIMATION_VICTORY_ZOMBIE_1 = "\uE711";
        public static final String GAME_ZOMBIE_ANIMATION_VICTORY_ZOMBIE_2 = "\uE712";
        public static final String GAME_ZOMBIE_ANIMATION_VICTORY_ZOMBIE_3 = "\uE713";
        public static final String GAME_ZOMBIE_ANIMATION_VICTORY_ZOMBIE_4 = "\uE714";
        public static final String GAME_ZOMBIE_ANIMATION_VICTORY_ZOMBIE_5 = "\uE715";
        public static final String GAME_ZOMBIE_ANIMATION_VICTORY_ZOMBIE_6 = "\uE716";
        public static final String GAME_ZOMBIE_ANIMATION_VICTORY_ZOMBIE_7 = "\uE717";
        public static final String GAME_ZOMBIE_ANIMATION_VICTORY_ZOMBIE_8 = "\uE718";
        public static final String GAME_ZOMBIE_ANIMATION_VICTORY_ZOMBIE_9 = "\uE719";
        public static final String GAME_ZOMBIE_ANIMATION_VICTORY_ZOMBIE_10 = "\uE71A";
        public static final String GAME_ZOMBIE_ANIMATION_VICTORY_ZOMBIE_11 = "\uE71B";
        public static final String GAME_ZOMBIE_ANIMATION_VICTORY_ZOMBIE_12 = "\uE71C";
        public static final String GAME_ZOMBIE_ANIMATION_VICTORY_ZOMBIE_13 = "\uE71D";

        public static final String GAME_NOAH_ANIMATION_DEFEAT_0 = "\uE860";
        public static final String GAME_NOAH_ANIMATION_DEFEAT_1 = "\uE861";
        public static final String GAME_NOAH_ANIMATION_DEFEAT_2 = "\uE862";
        public static final String GAME_NOAH_ANIMATION_DEFEAT_3 = "\uE863";
        public static final String GAME_NOAH_ANIMATION_DEFEAT_4 = "\uE864";
        public static final String GAME_NOAH_ANIMATION_DEFEAT_5 = "\uE865";
        public static final String GAME_NOAH_ANIMATION_DEFEAT_6 = "\uE866";
        public static final String GAME_NOAH_ANIMATION_DEFEAT_7 = "\uE867";
        public static final String GAME_NOAH_ANIMATION_DEFEAT_8 = "\uE868";
        public static final String GAME_NOAH_ANIMATION_DEFEAT_9 = "\uE869";
        public static final String GAME_NOAH_ANIMATION_DEFEAT_10 = "\uE86A";
        public static final String GAME_NOAH_ANIMATION_DEFEAT_11 = "\uE86B";
        public static final String GAME_NOAH_ANIMATION_DEFEAT_12 = "\uE86C";
        public static final String GAME_NOAH_ANIMATION_DEFEAT_13 = "\uE86D";
        public static final String GAME_NOAH_ANIMATION_DEFEAT_14 = "\uE86E";

        public static final String GAME_NOAH_ANIMATION_VICTORY_0 = "\uE870";
        public static final String GAME_NOAH_ANIMATION_VICTORY_1 = "\uE871";
        public static final String GAME_NOAH_ANIMATION_VICTORY_2 = "\uE872";
        public static final String GAME_NOAH_ANIMATION_VICTORY_3 = "\uE873";
        public static final String GAME_NOAH_ANIMATION_VICTORY_4 = "\uE874";
        public static final String GAME_NOAH_ANIMATION_VICTORY_5 = "\uE875";
        public static final String GAME_NOAH_ANIMATION_VICTORY_6 = "\uE876";
        public static final String GAME_NOAH_ANIMATION_VICTORY_7 = "\uE877";
        public static final String GAME_NOAH_ANIMATION_VICTORY_8 = "\uE878";
        public static final String GAME_NOAH_ANIMATION_VICTORY_9 = "\uE879";
        public static final String GAME_NOAH_ANIMATION_VICTORY_10 = "\uE87A";
        public static final String GAME_NOAH_ANIMATION_VICTORY_11 = "\uE87B";
        public static final String GAME_NOAH_ANIMATION_VICTORY_12 = "\uE87C";
        public static final String GAME_NOAH_ANIMATION_VICTORY_13 = "\uE87D";
        public static final String GAME_NOAH_ANIMATION_VICTORY_14 = "\uE87E";
        public static final String GAME_NOAH_ANIMATION_VICTORY_15 = "\uE87F";

        public static final String GAME_NOAH_GUN_AR = "\uE840";
        public static final String GAME_NOAH_GUN_SR = "\uE841";
        public static final String GAME_NOAH_GUN_PISTOL = "\uE842";
    }

    /**
     * Only PAPER Item
     */
    public static class Model {

        private Model() { throw new UnsupportedOperationException(); }

        public static final int BLANK = 10; // 0x0
        public static final int BLANK_GUI = 11; // 3x3

        /* Common Component */
        public static final int COMPONENT_LEFT = 20;
        public static final int COMPONENT_LEFT_DISABLE = 21;
        public static final int COMPONENT_RIGHT = 25;
        public static final int COMPONENT_RIGHT_DISABLE = 26;

        public static final int COMPONENT_BACK = 30;
        public static final int COMPONENT_CHECK = 35;
        public static final int COMPONENT_X = 36;

        /* Channel Icons */
        public static final int CHANNEL_LOBBY = 100;
        public static final int CHANNEL_UNRELEASED = 110;
        public static final int CHANNEL_UNRELEASED_LOCK = 111;
        public static final int CHANNEL_ZOMBIE = 120;
        public static final int CHANNEL_ZOMBIE_LOCK = 121;
        public static final int CHANNEL_ZOMBIE_TEST = 125;
        public static final int CHANNEL_ZOMBIE_TEST_LOCK = 126;
        public static final int CHANNEL_PVE_SURVIVAL = 130;
        public static final int CHANNEL_PVE_SURVIVAL_LOCK = 131;
        public static final int CHANNEL_NOAH = 140;
        public static final int CHANNEL_NOAH_LOCK = 141;
        public static final int CHANNEL_NEXTGEN = 190;


        /* (Common) Lobby Icons */
        public static final int LOBBY_ICON_MANAGEMENT = 300;
        public static final int LOBBY_ICON_START = 301;
        public static final int LOBBY_ICON_DISCORD = 302;
        public static final int LOBBY_ICON_CHANNEL = 303;
        public static final int LOBBY_ICON_SETTING = 304;
        public static final int LOBBY_ICON_CHATCOOL = 305;
        public static final int LOBBY_ICON_LOCK = 306;
        public static final int LOBBY_ICON_VOTE = 310;
        public static final int LOBBY_ICON_VOTE_CHECK = 311;
        public static final int LOBBY_ICON_GAMELIST = 320;
        public static final int LOBBY_ICON_PARTY_INFO = 325;
        public static final int LOBBY_ICON_PARTY_INVITE = 326;
        public static final int LOBBY_ICON_PARTY_EXIT = 327;

        /* (Zombie) Lobby Icons */
        public static final int ZOMBIE_ICON_PRIMARY = 1000;
        public static final int ZOMBIE_ICON_SECONDARY = 1001;
        public static final int ZOMBIE_ICON_PROXIMITY = 1002;
        public static final int ZOMBIE_ICON_EQUIPMENT = 1003;
        public static final int ZOMBIE_ICON_PRESET = 1004;
        public static final int ZOMBIE_ICON_CANCEL = 1005;

        public static final int ZOMBIE_INVENTORY_MYWEAPONS_1 = 1050;
        public static final int ZOMBIE_INVENTORY_MYWEAPONS_2 = 1051;
        public static final int ZOMBIE_INVENTORY_SHOP = 1052;
        public static final int ZOMBIE_INVENTORY_SKIN = 1053;
        public static final int ZOMBIE_INVENTORY_GACHA = 1054;
        public static final int ZOMBIE_INVENTORY_FASHION = 1055;
    }
}
