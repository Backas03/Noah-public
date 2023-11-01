package org.caramel.backas.noah.prefix;

import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.caramel.backas.noah.user.UserData;
import org.caramel.backas.noah.user.UserDataLoader;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

@SerializableAs("PrefixData")
public class PrefixData implements UserData, ConfigurationSerializable {

    public static final String KEY = "prefix";

    public static UserDataLoader<PrefixData> loader() {
        return user -> user.getDataContainer().loadYaml().getSerializable(KEY, PrefixData.class, new PrefixData(null, null));
    }

    private String current;
    private Set<String> keys;

    public PrefixData(String current, List<String> keys) {
        this.current = current;
        if (keys == null) this.keys = null;
        else this.keys = new HashSet<>(keys);
    }

    public void addPrefix(String namespacedKey) {
        if (keys == null) keys = new HashSet<>();
        keys.add(namespacedKey);
    }

    public String getCurrentNamespacedKey() {
        return current;
    }

    public void removePrefix(String namespacedKey) {
        if (keys == null) return;
        keys.remove(namespacedKey);
        if (keys.isEmpty()) keys = null;
        if (current.equals(namespacedKey)) current = null; // 현재 칭호일 시 칭호 장착 해제
    }

    public @NotNull Set<String> getNamespacedKeys() {
        return keys == null ? new HashSet<>() : keys;
    }

    public void setPrefix(String namespacedKey) {
        this.current = namespacedKey;
    }

    public void setPrefix(String namespacedKey, boolean checkExists) {
        if (checkExists && (keys == null || !keys.contains(namespacedKey))) return;
        setPrefix(namespacedKey);
    }

    public Component getCurrentPrefix() {
        return current == null ? null : Prefix.getComponent(current);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("current", current);
        data.put("keys", keys == null ? null : new ArrayList<>(keys));
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
    public static PrefixData deserialize(Map<String, Object> data) {
        return new PrefixData(
                (String) data.get("current"),
                (List<String>) data.get("keys")
        );
    }
}
