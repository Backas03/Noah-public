package org.caramel.backas.noah.skin.model.reaper.test.effect;

import kr.lostwar.fmj.api.events.WeaponReloadEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.caramel.backas.noah.skin.SkinEffect;
import org.caramel.backas.noah.skin.Skin;

public class EffectReaperTestReload extends SkinEffect {

    public static final String EFFECT_ID = "EFFECT_REAPER_TEST_RELOAD";

    public EffectReaperTestReload(Skin skin, int cost) {
        super(skin, EFFECT_ID, cost);
    }

    @EventHandler
    public void onReload(WeaponReloadEvent event) {
        Player player = event.getPlayer();
        this.hasEffectRun(event.getWeapon(), player, () -> {
            player.sendMessage("Reloading...");
        });
    }
}
