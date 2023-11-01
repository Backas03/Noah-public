package org.caramel.backas.noah.api.party;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.api.event.party.PartyDeleteEvent;
import org.caramel.backas.noah.api.event.party.PartyInviteResponseEvent;
import org.caramel.backas.noah.api.event.party.PartyLeaveEvent;
import org.caramel.backas.noah.api.event.party.PartyOwnerTransferEvent;
import org.caramel.backas.noah.api.game.matching.MatchingPool;
import org.caramel.backas.noah.api.user.User;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Random;

public class PartyManager {

    private static Title errorTitle(String title, String subTitle) {
        return Title.title(
            Component.text(title, NamedTextColor.RED),
            Component.text(subTitle, NamedTextColor.GRAY),
            Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(2000), Duration.ofMillis(500))
        );
    }

    private static Title title(TextComponent title, TextComponent subTitle) {
        return Title.title(
            title, subTitle,
            Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(2000), Duration.ofMillis(500))
        );
    }

    private static Title title(TextComponent title) {
        return Title.title(title, Component.empty());
    }

    public static Party createNewParty(User user) {
        if (user.hasParty()) {
            user.sendTitle(errorTitle("파티 생성 불가", "소속된 파티가 있습니다"));
            return null;
        }
        Party party = new Party(user);
        user.setParty(party);
        user.sendTitle( title(Component.text("파티 생성 완료", NamedTextColor.GREEN)) );
        return party;
    }

    public static void invite(User invitor, User invitee) {
        if (!invitor.hasParty()) {
            invitor.sendTitle(errorTitle("파티 초대 실패", "소속된 파티가 없습니다"));
            return;
        }
        Party party = invitor.getParty();
        if (party.isParticipated(invitee)) {
            invitor.sendTitle(errorTitle("파티 초대 불가", "이미 파티에 소속된 유저입니다"));
            return;
        }
        if (MatchingPool.isMatching(party)) {
            invitor.sendTitle(errorTitle("파티 초대 불가", "파티가 게임 매칭 대기열에 입장된 상태입니다"));
            return;
        }
        Party targetParty = invitee.getParty();
        if (targetParty != null && MatchingPool.isMatching(targetParty)) {
            invitor.sendTitle(errorTitle("파티 초대 불가", "상대방이 게임 매칭 대기열에 입장된 상태입니다"));
            return;
        }
        if (invitee.hasInvitedParty(party)) {
            invitor.sendTitle(errorTitle("파티 초대 실패", "이미 초대장이 발송된 상태입니다"));
            return;
        }
        invitee.addInvitedParty(party);
        invitee.sendTitle(title(
            Component.text("새로운 파티 초대가 있습니다", NamedTextColor.GREEN),
            Component.text("파티 메뉴에서 초대장을 확인하실 수 있습니다", NamedTextColor.GRAY)
        ));
        party.sendTitle(title(
            Component.text("파티 초대장이 전송 되었습니다", NamedTextColor.YELLOW),
            Component.text().append(
                Component.text(invitor.getName(), NamedTextColor.GREEN),
                Component.text(" >> ", NamedTextColor.GRAY),
                Component.text(invitee.getName(), NamedTextColor.AQUA)
            ).build()
        ));
        Bukkit.getScheduler().runTaskLater(Noah.getInstance(), () -> invitee.getInvitedParties().remove(party), 180 * 20);
    }

    public static void responseInvite(User invitee, Party party, boolean accept) {
        if (!invitee.hasInvitedParty(party)) {
            invitee.sendTitle(errorTitle("파티 초대 응답 실패", "파티 초대장이 만료된 상태입니다"));
            return;
        }
        if (accept) {
            if (MatchingPool.isMatching(party)) {
                invitee.sendTitle(errorTitle("파티 초대 응답 실패", "초대받은 파티가 게임 매칭 대기열에 입장된 상태입니다"));
                return;
            }
            if (invitee.hasParty()) {
                leaveParty(invitee);
            }
            invitee.getInvitedParties().remove(party);
            party.getMembers().add(invitee);
            invitee.setParty(party);
            party.sendTitle(
                title(
                    Component.text("파티 입장", NamedTextColor.GREEN),
                    Component.text().append(
                        Component.text("새로운 파티원", NamedTextColor.AQUA),
                        Component.text(" - ", NamedTextColor.GRAY),
                        Component.text(invitee.getName(), NamedTextColor.WHITE)
                    ).build()
                )
            );
        } else {
            invitee.getInvitedParties().remove(party);
            invitee.sendTitle(
                title(
                    Component.text("파티 초대 거절 완료", NamedTextColor.RED),
                    Component.text().append(
                        Component.text(party.getOwner().getName(), NamedTextColor.AQUA),
                        Component.text(" 님의 파티", NamedTextColor.GRAY)
                    ).build()
                )
            );
        }
        Bukkit.getPluginManager().callEvent(new PartyInviteResponseEvent(party, invitee, accept));
    }

    public static void leaveParty(User user) {
        if (!user.hasParty()) {
            user.sendTitle(errorTitle("파티 퇴장 실패", "소속된 파티가 없습니다"));
            return;
        }
        if (user.isInGame()) {
            user.sendTitle(errorTitle("파티 퇴장 실패", "게임이 시작되어 퇴장하실 수 없습니다"));
            return;
        }
        Party party = user.getParty();
        if (MatchingPool.isMatching(party)) {
            MatchingPool.dequeue(party);
        }
        if (party.getAllMembers().size() == 1) {
            deleteParty(party);
        } else {
            if (party.isOwner(user)) {
                User newOwner = new ArrayList<>(party.getMembers()).get(new Random().nextInt(party.getMembers().size()));
                party.setOwner(newOwner);
                party.getMembers().remove(newOwner);
                party.sendTitle(
                    title(
                        Component.text("파티장이 파티를 떠났습니다", NamedTextColor.YELLOW),
                        Component.text().append(
                            Component.text("새로운 파티장", NamedTextColor.AQUA),
                            Component.text(" - ", NamedTextColor.GRAY),
                            Component.text(newOwner.getName(), NamedTextColor.WHITE)
                        ).build()
                    )
                );
                Bukkit.getPluginManager().callEvent(new PartyOwnerTransferEvent(party, user, newOwner));
            } else {
                party.getMembers().remove(user);
            }
            user.setParty(null);
        }
        user.sendTitle( title(Component.text("파티 퇴장 완료", NamedTextColor.YELLOW)) );
        Bukkit.getPluginManager().callEvent(new PartyLeaveEvent(party, user));
    }

    public static void deleteParty(Party party) {
        Bukkit.getPluginManager().callEvent(new PartyDeleteEvent(party));
        party.getAllMembers().forEach(user -> user.setParty(null));
        removeAllUserInvites(party);
    }

    public static void removeAllUserInvites(Party party) {
        User.getAll().forEach(user -> user.getInvitedParties().remove(party));
    }

}
