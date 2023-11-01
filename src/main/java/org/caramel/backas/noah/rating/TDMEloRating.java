package org.caramel.backas.noah.rating;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.caramel.backas.noah.user.UserData;
import org.caramel.backas.noah.user.UserDataLoader;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@SerializableAs("TDMEloRating")
public class TDMEloRating implements UserData, ConfigurationSerializable {

    public static final int MAX_K = 100;
    public static final int DEFAULT_RATING = 2500;
    public static final String KEY = "TeamDeathMatchRating";

    public static UserDataLoader<TDMEloRating> loader() {
        return user -> user.getDataContainer().loadYaml().getSerializable(KEY, TDMEloRating.class, new TDMEloRating(DEFAULT_RATING));
    }

    public TDMEloRating(int rating) {
        this.rating = rating;
    }

    public int rating;

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("rating", rating);
        return data;
    }

    @Override
    public boolean save(File file) throws Exception {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        yaml.set(KEY, this);
        yaml.save(file);
        return true;
    }

    public static TDMEloRating deserialize(Map<String, Object> data) {
        return new TDMEloRating((int) data.get("rating"));
    }
}
