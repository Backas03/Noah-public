package org.caramel.backas.noah.api.user.model;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Point implements ConfigurationSerializable {

    private int value;

    public Point(int value) {
        this.value = value;
    }

    public int get() {
        return value;
    }

    public void set(int value) {
        this.value = value;
    }

    public void add(int amount) {
        value += amount;
    }

    public void remove(int amount) {
        value = Math.max(0, value - amount);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("value", value);
        return data;
    }

    public static Point deserialize(Map<String, Object> data) {
        return new Point(
                (int) data.get("value")
        );
    }

}
