package org.caramel.backas.noah.skin.model.reaper.test.effect;

import kr.lostwar.fmj.api.events.WeaponReloadEndEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.caramel.backas.noah.skin.SkinEffect;
import org.caramel.backas.noah.skin.Skin;

public class EffectReaperTestReloadEnd extends SkinEffect {

    public static final String EFFECT_ID = "EFFECT_REAPER_TEST_RELOAD_END";

    public EffectReaperTestReloadEnd(Skin skin, int cost) {
        super(skin, EFFECT_ID, cost);
    }

    @EventHandler
    public void onReloadEnd(WeaponReloadEndEvent event) {
        Player player = event.getPlayer();
        this.hasEffectRun(event.getWeapon(), event.getPlayer(), () -> {
            player.sendMessage(Component.text("Reload Completed!"));
        });
    }
}
