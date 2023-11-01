package org.caramel.backas.noah.kda;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.caramel.backas.noah.user.UserData;
import org.caramel.backas.noah.user.UserDataLoader;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@SerializableAs("KDA")
public class KDA implements UserData, ConfigurationSerializable {

    public static final String KEY = "KDA";

    public static UserDataLoader<KDA> loader() {
        return user -> user.getDataContainer().loadYaml().getSerializable(KEY, KDA.class, new KDA(0, 0, 0));
    }

    public int kills;
    public int deaths;
    public int assists;

    public KDA(int kills, int deaths, int assists) {
        this.kills = kills;
        this.deaths = deaths;
        this.assists = assists;
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
        data.put("kills", kills);
        data.put("deaths", deaths);
        data.put("assists", assists);
        return data;
    }

    public static KDA deserialize(Map<String, Object> data) {
        return new KDA(
                (int) data.get("kills"),
                (int) data.get("deaths"),
                (int) data.get("assists")
        );
    }
}
