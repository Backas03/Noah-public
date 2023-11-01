package org.caramel.backas.noah.tier;

import net.kyori.adventure.text.Component;

public class Tier {

    public static final Tier NEPTUNE = new Tier(Component.text("넵튠"));
    public static final Tier NOAH = new Tier(Component.text("노아"));
    public static final Tier JUPITER = new Tier(Component.text("주피터"));
    public static final Tier MARS = new Tier(Component.text("마스"));
    public static final Tier BENUS = new Tier(Component.text("비너스"));
    public static final Tier MERQURI = new Tier(Component.text("머큐리"));
    public static final Tier KING = new Tier(Component.text("킹"));
    public static final Tier THE_KING = new Tier(Component.text("더킹"));

    public final Component name;

    public Tier(Component name) {
        this.name = name;
    }

}
