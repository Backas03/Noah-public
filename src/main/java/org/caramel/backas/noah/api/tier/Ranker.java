package org.caramel.backas.noah.api.tier;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@SerializableAs("Ranker")
@AllArgsConstructor
public class Ranker implements ConfigurationSerializable {

    private final UUID uniqueId;
    private final String name;
    @Setter
    private int rating;

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("uniqueId", uniqueId.toString());
        data.put("name", name);
        data.put("rating", rating);
        return data;
    }

    public static Ranker deserialize(Map<String, Object> data) {
        return new Ranker(
                UUID.fromString((String) data.get("uniqueId")),
                (String) data.get("name"),
                (int) data.get("rating")
        );
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof String s && s.equals(name)) || (obj instanceof UUID uuid && uuid.equals(uniqueId));
    }
}
