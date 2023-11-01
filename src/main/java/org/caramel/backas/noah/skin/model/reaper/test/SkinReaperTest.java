package org.caramel.backas.noah.skin.model.reaper.test;

import org.caramel.backas.noah.skin.Skin;
import org.caramel.backas.noah.skin.model.reaper.test.effect.EffectReaperTestReload;
import org.caramel.backas.noah.skin.model.reaper.test.effect.EffectReaperTestReloadEnd;
import org.caramel.backas.noah.skin.model.reaper.test.effect.EffectReaperTestShoot;

public class SkinReaperTest extends Skin {

    public static final String MODEL_KEY = "Reaper";

    public SkinReaperTest() {
        this.addEffect(new EffectReaperTestShoot(this, 0));
        this.addEffect(new EffectReaperTestReload(this, 0));
        this.addEffect(new EffectReaperTestReloadEnd(this, 0));
    }

    @Override
    public String getOriginKey() {
        return OriginKey.REAPER;
    }

    @Override
    public String getModelKey() {
        return MODEL_KEY;
    }
}
