package org.caramel.backas.noah.noahpoint;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.caramel.backas.noah.user.UserData;
import org.caramel.backas.noah.user.UserDataLoader;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@SerializableAs("NoahPoint")
public class NoahPoint implements UserData, ConfigurationSerializable {

    public static final String KEY = "NoahPoint";

    public static UserDataLoader<NoahPoint> loader() {
        return user -> user.getDataContainer().loadYaml().getSerializable(KEY, NoahPoint.class, new NoahPoint());
    }

    public int value;

    public NoahPoint() {
        value = 0;
    }

    public NoahPoint(int value) {
        this.value = value;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("value", value);
        return data;
    }

    @Override
    public boolean save(File file) throws Exception {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        yaml.set(KEY, this);
        yaml.save(file);
        return true;
    }

    public static NoahPoint deserialize(Map<String, Object> data) {
        return new NoahPoint(
                (int) data.get("value")
        );
    }
}
