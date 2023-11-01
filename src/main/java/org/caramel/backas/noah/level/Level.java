package org.caramel.backas.noah.level;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.caramel.backas.noah.user.UserData;
import org.caramel.backas.noah.user.UserDataLoader;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@SerializableAs("Level")
public class Level implements UserData, ConfigurationSerializable {

    public static final String KEY = "Level";

    public static UserDataLoader<Level> loader() {
        return user -> user.getDataContainer().loadYaml().getSerializable(KEY, Level.class, new Level(0));
    }

    private @Getter @Setter int exp;

    public Level(int exp) {
        this.exp = exp;
    }

    public boolean isMaxLevel() {
        return ExpData.getLevel(exp) == ExpData.MAX_LEVEL;
    }

    public int getLevel() {
        return ExpData.getLevel(exp);
    }

    @Override
    public boolean save(File file) throws Exception {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        yaml.set(KEY, this);
        yaml.save(file);
        return true;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("exp", exp);
        return data;
    }

    public static Level deserialize(Map<String, Object> data) {
        return new Level(
                (int) data.get("exp")
        );
    }
}
