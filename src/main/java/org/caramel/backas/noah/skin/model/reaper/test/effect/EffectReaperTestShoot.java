package org.caramel.backas.noah.skin.model.reaper.test.effect;

import kr.lostwar.fmj.api.events.WeaponShootEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.caramel.backas.noah.skin.SkinEffect;
import org.caramel.backas.noah.skin.Skin;

public class EffectReaperTestShoot extends SkinEffect {

    public static final String EFFECT_ID = "EFFECT_REAPER_TEST_SHOOT";

    public EffectReaperTestShoot(Skin skin, int cost) {
        super(skin, EFFECT_ID, cost);
    }

    @EventHandler
    public void onShoot(WeaponShootEvent event) {
        Player player = event.getPlayer();
        this.hasEffectRun(event.getWeapon(), player, () -> {
            player.sendMessage(Component.text("!!"));
        });
    }
}
