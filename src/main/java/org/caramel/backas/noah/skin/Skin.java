package org.caramel.backas.noah.skin;

import kr.lostwar.fmj.api.FMJ;
import kr.lostwar.fmj.api.weapon.Weapon;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.caramel.backas.noah.Noah;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class Skin {

    public static final class OriginKey {

        public static final String REAPER = "Reaper";
        public static final String GHOST = "ghost";
        public static final String AWPER = "awper";
        public static final String SCOUT = "scout";

        private OriginKey() {
            throw new UnsupportedOperationException();
        }

    }

    public static Skin find(String key) {
        return REGISTERED.get(key);
    }

    public static Skin find(Weapon weapon) {
        return weapon == null ? null : find(weapon.getName());
    }

    public static void register(Skin model) {
        REGISTERED.put(model.getModelKey(), model);
    }

    public static Collection<Skin> getSkins() {
        return REGISTERED.values();
    }

    private static final Map<String, Skin> REGISTERED = new HashMap<>();

    private final Map<String, SkinEffect> skinEffect = new HashMap<>();

    public abstract String getOriginKey();

    public abstract String getModelKey();

    public void addEffect(SkinEffect effect) {
        Bukkit.getServer().getPluginManager().registerEvents(effect, Noah.getInstance());
        skinEffect.put(effect.getId(), effect);
    }

    public Map<String, SkinEffect> getSkinEffects() {
        return skinEffect;
    }

    public void removeEffect(String effectId) {
        skinEffect.computeIfPresent(effectId, (type, effect) -> {
            HandlerList.unregisterAll(effect);
            return null;
        });
    }

    public boolean hasEffect(String effectId) {
        return skinEffect.containsKey(effectId);
    }

    public SkinEffect getEffect(String effectId) {
        return skinEffect.get(effectId);
    }

    public Collection<SkinEffect> getEffects() {
        return skinEffect.values();
    }

    public Optional<Weapon> getWeapon() {
        return Optional.ofNullable(FMJ.findWeapon(getModelKey()));
    }

    public Optional<Weapon> getOriginWeapon() {
        return Optional.ofNullable(FMJ.findWeapon(getOriginKey()));
    }
}
