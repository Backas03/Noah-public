package org.caramel.backas.noah.game.tdm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TDMGameMapRegistry {

    private static final List<TDMGameMap> MAPS = new ArrayList<>();

    public static void register(TDMGameMap map) {
        MAPS.add(map);
    }

    public static TDMGameMap random() {
        List<TDMGameMap> maps = new ArrayList<>(MAPS);
        Collections.shuffle(maps);
        for (TDMGameMap map : maps) {
            if (map.isEnabled()) {
                return map;
            }
        }
        return null;
        /*
        while (!maps.isEmpty()) {
            int index = (int) (Math.random() * (MAPS.size() - 1));
            IGameMap map = maps.get(index);
            Bukkit.broadcast(Component.text("random() - " + map.isEnabled() + " - " + map.getName()));
            if (map.isEnabled()) {
                return map;
            }
            maps.remove(index);
        }
        return null;

         */
    }
}
