package org.caramel.backas.noah.api.game.rating;


import lombok.AllArgsConstructor;
import lombok.Setter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Setter
@SerializableAs("GlickoRating")
@AllArgsConstructor
public class Rating implements ConfigurationSerializable {

    private int rating;

    public Rating() {
        rating = EloRatingCalculator.DEFAULT_RATING;
    }

    public int get() {
        return rating;
    }

    public int getK() {
        int K;
        if (rating <= 1100) {
            K = 32;
        } else if (rating <= 1500) {
            K = 24;
        } else {
            K = 16;
        }
        return K;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("rating", rating);
        return data;
    }

    public static Rating deserialize(Map<String, Object> data) {
        return new Rating(
                (int) data.get("rating")
        );
    }

}