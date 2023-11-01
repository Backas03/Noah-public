package org.caramel.backas.noah.game.tdm;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.caramel.backas.noah.Lobby;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.game.GameException;
import org.caramel.backas.noah.game.GameLauncher;
import org.caramel.backas.noah.game.IGame;
import org.caramel.backas.noah.game.IGameMap;
import org.caramel.backas.noah.user.User;
import org.caramel.backas.noah.util.Position;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.Set;

public class TDMGameLauncher implements GameLauncher<GameTDM> {

    public static final Position ICON_POS = new Position(-51, 26, 2);
    public static final Position ARMOR_STAND_POS = new Position(-49.5, 28, 0.5);

    @Override
    public String getName() {
        return TDMGameOption.NAME;
    }

    @Override
    public GameTDM start(IGameMap map, Set<User> players) {
        GameTDM game = new GameTDM(map);
        Bukkit.getPluginManager().registerEvents(game, Noah.getInstance());
        game.onStart(players);
        return game;
    }

    @Override
    public void onOver(IGame game) {
        game.onOver();
        if (game instanceof Listener listener) {
            HandlerList.unregisterAll(listener); // unregister bukkit event listeners
        }
    }

    @Override
    public @NotNull IGameMap selectMap() throws GameException {
        TDMGameMap map = TDMGameMapRegistry.random();
        while (map != null) {
            if (!map.loadWorld() || Bukkit.getWorld(map.getWorldName()) == null) {
                map.setEnable(false); // 꼭 필요!
                map.unloadWorld();
                map = TDMGameMapRegistry.random();
                continue;
            }
            ItemStack icon = map.getIcon();
            if (icon != null) {
                Location location = ICON_POS.toLocation(Lobby.getWorld());
                if (!location.getChunk().isLoaded()) {
                    location.getChunk().load();
                }
                Collection<ItemFrame> entities = location.getNearbyEntitiesByType(ItemFrame.class, 2);
                if (!entities.isEmpty()) {
                    ItemFrame itemFrame = entities.iterator().next();
                    itemFrame.setVisible(false);
                    itemFrame.setFixed(true);
                    itemFrame.setItemDropChance(.0F);
                    itemFrame.setRotation(Rotation.NONE);
                    itemFrame.setItem(icon);
                }
            }
            Location armorStandLocation = ARMOR_STAND_POS.toLocation(Lobby.getWorld());
            if (!armorStandLocation.getChunk().isLoaded()) {
                armorStandLocation.getChunk().load();
            }
            Collection<ArmorStand> armorStands = armorStandLocation.getNearbyEntitiesByType(ArmorStand.class, 2);
            if (!armorStands.isEmpty()) {
                ArmorStand armorStand =  armorStands.iterator().next();
                armorStand.customName(map.getName());
            }
            return map;
        }
        throw new GameException(Component.text("활성화 된 맵이 없습니다."));
    }
}
