package org.caramel.backas.noah.api.party;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.title.Title;
import org.caramel.backas.noah.api.game.AbstractGame;
import org.caramel.backas.noah.api.user.User;

import java.util.HashSet;
import java.util.Set;

public class Party {

    @Getter @Setter
    private User owner;
    private final Set<User> members = new HashSet<>();

    public Party(User owner) {
        this.owner = owner;
    }

    public Set<User> getMembers() {
        return members;
    }

    public Set<User> getAllMembers() {
        Set<User> value = new HashSet<>(members);
        value.add(owner);
        return value;
    }

    public boolean isOwner(User user) {
        return owner.equals(user);
    }

    public boolean isParticipated(User user) {
        return getAllMembers().contains(user);
    }

    public boolean isMember(User user) {
        return members.contains(user);
    }

    public void broadcastTitle(String title, String subTitle, int in, int stay, int out) {
        getAllMembers().forEach(user -> user.sendTitle(title, subTitle, in, stay, out));
    }

    public void sendTitle(Title title) {
        getAllMembers().forEach(user -> user.sendTitle(title));
    }

}
