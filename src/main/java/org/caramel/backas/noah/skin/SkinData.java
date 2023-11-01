package org.caramel.backas.noah.skin;

import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.caramel.backas.noah.user.UserData;
import org.caramel.backas.noah.user.UserDataLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SerializableAs("SkinData")
public class SkinData implements UserData, ConfigurationSerializable {

    public static UserDataLoader<SkinData> loader() {
        return user -> user.getDataContainer().loadYaml().getSerializable(KEY, SkinData.class, new SkinData());
    }
    private static final String KEY = "SkinData";

    /* TODO: key = 기본 모델 fmj key, 총기 선택창에서 쉬프트 클릭 시 스킨 선택창으로 이동, 클릭시 현재 선택된 스킨으로 함 */
    private final Map<String, String> currentSkin;

    /* skin key, skin effect, effect enabled */
    private final Map<String, Map<String, Boolean>> skinEffectData;

    public SkinData() {
        currentSkin = new HashMap<>();
        skinEffectData = new HashMap<>();
    }

    public SkinData(Map<String, String> currentSkin, Map<String, Map<String, Boolean>> skinEffectData) {
        this.currentSkin = currentSkin != null ? currentSkin : new HashMap<>();
        this.skinEffectData = skinEffectData != null ? skinEffectData : new HashMap<>();
    }

    public void grantEffect(Skin skin, String effectId, boolean enable) throws SkinException {
        if (!skin.hasEffect(effectId)) throw new SkinException(Component.text("스킨에 해당 이펙트가 없습니다."));
        if (!hasSkin(skin)) throw new SkinException(Component.text("해당 스킨을 소유하고 있지 않습니다."));
        Boolean b = skinEffectData.get(skin.getModelKey()).putIfAbsent(effectId, enable);
        if (b != null) throw new SkinException(Component.text("이미 해당 이펙트를 보유중입니다."));
    }

    public void setEffectEnable(Skin skin, String effectId, boolean enable) throws SkinException {
        if (!hasSkin(skin)) throw new SkinException(Component.text("해당 스킨을 소유하고 있지 않습니다."));
        if (!skin.hasEffect(effectId)) throw new SkinException(Component.text("스킨에 해당 이펙트가 없습니다."));
        if (!hasEffect(skin, effectId)) throw new SkinException(Component.text("스킨에 해당 이펙트를 보유하고 있지 않습니다."));
        skinEffectData.get(skin.getModelKey()).put(effectId, enable);
    }

    public Set<Skin> getSkins() {
        Set<Skin> skins = new HashSet<>();
        for (String key : skinEffectData.keySet()) {
            Skin skin = Skin.find(key);
            if (skin != null) skins.add(skin);
        }
        return skins;
    }

    public void forgetEffect(Skin skin, String effectId) throws SkinException {
        if (!skin.hasEffect(effectId)) throw new SkinException(Component.text("스킨에 해당 이펙트가 없습니다. effectId=" + effectId));
        if (!hasSkin(skin)) throw new SkinException(Component.text("해당 스킨을 소유하고 있지 않습니다."));
        if (skinEffectData.get(skin.getModelKey()).containsKey(effectId)) {
            skinEffectData.get(skin.getModelKey()).remove(effectId);
            return;
        }
        throw new SkinException(Component.text("해당 이펙트를 보유하고 있지 않습니다."));
    }

    public void addSkin(Skin skin) throws SkinException {
        if (hasSkin(skin)) throw new SkinException(Component.text("이미 해당 스킨을 보유 중 입니다."));
        skinEffectData.put(skin.getModelKey(), new HashMap<>());
    }

    public void removeSkin(Skin skin) throws SkinException {
        if (!hasSkin(skin)) throw new SkinException(Component.text("해당 스킨을 보유하고 있지 않습니다."));
        skinEffectData.remove(skin.getModelKey());
    }

    public String getCurrentSkin(String originKey) {
        return currentSkin.getOrDefault(originKey, originKey);
    }

    public void setCurrentSkin(Skin skin) throws SkinException {
        if (!hasSkin(skin)) throw new SkinException(Component.text("해당 스킨을 보유하고 있지 않습니다."));
        currentSkin.put(skin.getOriginKey(), skin.getModelKey());
    }

    public void resetCurrentSkin(String originKey) {
        currentSkin.remove(originKey);
    }

    public boolean hasSkin(Skin skin) {
        return skinEffectData.containsKey(skin.getModelKey());
    }

    public boolean hasEffect(Skin skin, String effectId) {
        return hasSkin(skin) && skinEffectData.get(skin.getModelKey()).containsKey(effectId);
    }

    public @Nullable Boolean isEffectEnabled(Skin skin, String effectId) {
        if (!skin.hasEffect(effectId) || !hasSkin(skin)) return null;
        return skinEffectData.get(skin.getModelKey()).get(effectId);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("currentSkins", currentSkin);
        data.put("skinEffectData", skinEffectData);
        return data;
    }

    @Override
    public boolean save(File file) throws Exception {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        yaml.set(KEY, this);
        yaml.save(file);
        return true;
    }

    @SuppressWarnings("unchecked")
    public static SkinData deserialize(Map<String, Object> data) {
        return new SkinData(
                (Map<String, String>) data.get("currentSkins"),
                (Map<String, Map<String, Boolean>>) data.get("skinEffectData")
        );
    }
}
