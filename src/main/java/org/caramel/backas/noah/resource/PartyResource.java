package org.caramel.backas.noah.resource;


import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.caramel.backas.noah.deprecated.ResourceIds;
import org.caramel.backas.noah.util.ItemStackUtil;

import static net.kyori.adventure.text.Component.text;

public class PartyResource {

    public static final String GUI_NAME_PARTY_INFO = "파티 정보"; // 파티 구성원 목록을 확인할 수 있는 GUI
    public static final String GUI_NAME_PARTY_INVITES = "파티 초대 목록"; // 파티 초대장을 확인할 수 있는 GUI
    public static final String GUI_NAME_PARTY_INFO_MENU = "파티 정보 메뉴"; // 플레이어가 파티에 소속중일때 shift+f 시 띄워지는 GUI


    public static ItemStack getInvitesItem() {
        return ItemStackUtil.get(Material.PAPER,
            text("파티 초대장 목록", NamedTextColor.YELLOW),
            text("클릭시 파티 초대장 목록을 확인합니다.", NamedTextColor.GRAY),
            (meta) -> meta.setCustomModelData(ResourceIds.Model.LOBBY_ICON_PARTY_INVITE)
        );
    }

    public static ItemStack getMatchItem() {
        return ItemStackUtil.get(Material.PAPER,
            text("게임 목록", ResourceIds.Styles.AQUA_NO_ITALIC),
            text("클릭시 플레이할 게임을 선택하는 창으로 이동합니다.", ResourceIds.Styles.GRAY_NO_ITALIC),
            (meta) -> meta.setCustomModelData(ResourceIds.Model.LOBBY_ICON_GAMELIST)
        );
    }

    public static ItemStack getInfoItem() {
        return ItemStackUtil.get(Material.PAPER,
            text("파티 정보", ResourceIds.Styles.YELLOW_NO_ITALIC),
            text("클릭시 파티 정보를 확인하는 창으로 이동합니다.", ResourceIds.Styles.GRAY_NO_ITALIC),
            (meta) -> meta.setCustomModelData(ResourceIds.Model.LOBBY_ICON_PARTY_INFO)
        );
    }

    public static ItemStack getLeaveItem() {
        return ItemStackUtil.get(Material.PAPER,
            text("파티 탈퇴", ResourceIds.Styles.RED_NO_ITALIC),
            text("클릭시 파티를 탈퇴합니다.", ResourceIds.Styles.GRAY_NO_ITALIC),
            (meta) -> meta.setCustomModelData(ResourceIds.Model.LOBBY_ICON_PARTY_EXIT)
        );
    }

    public static ItemStack getCancelMatchingItem() {
        return ItemStackUtil.get(Material.RED_BED,
            text("매칭 취소", ResourceIds.Styles.RED_NO_ITALIC),
            text("클릭하여 매칭을 취소합니다.", ResourceIds.Styles.GRAY_NO_ITALIC)
        );
    }

    public static ItemStack getCannotCancelMatchingItem() {
        return ItemStackUtil.get(Material.PAPER,
            text("게임 목록", ResourceIds.Styles.GREEN_NO_ITALIC),
            text("파티장이 아니므로 게임을 선택하실 수 없습니다.", ResourceIds.Styles.GRAY_NO_ITALIC),
            (meta) -> meta.setCustomModelData(ResourceIds.Model.LOBBY_ICON_GAMELIST)
        );
    }

}
