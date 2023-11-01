package org.caramel.backas.noah.api.config;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.util.FileUtil;

import java.io.File;
import java.io.IOException;

public class Config {

    private final File file;
    private YamlConfiguration yaml;

    public Config(File file) {
        try {
            FileUtil.checkAndCreateFile(file);
        } catch (IOException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(Noah.getInstance());
        }
        this.file = file;
    }

    public void load() {
        yaml = YamlConfiguration.loadConfiguration(file);
    }

    public void save() throws IOException {
        if (yaml == null) return;
        yaml.save(file);
    }

    public YamlConfiguration get() {
        return yaml;
    }

    public class Value<V> {

        private final String key;
        private Mapper<V> mapper;
        private V def;

        public Value(String key) {
            this.key = key;
        }

        public Value(String key, V def) {
            this(key);
            this.def = def;
        }

        public Value(String key, V def, Mapper<V> mapper) {
            this(key, def);
            this.mapper = mapper;
        }

        public V get() {
            Object data = yaml.get(key);
            if (data == null && def != null) {
                set(def);
                return def;
            }
            if (data == null) return null;
            if (mapper != null) return mapper.get(data);
            return (V) data;
        }

        public void set(V data) {
            if (mapper != null && data != null) {
                yaml.set(key, mapper.set(data));
                return;
            }
            yaml.set(key, data);
        }

    }

    public interface Mapper<V> {

        Object set(V value);
        V get(Object value);

    }

}
