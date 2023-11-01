package org.caramel.backas.noah.api.user.model;

import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("NoahPoint")
public class NoahPoint extends Point {

    public static final String KEY = "NoahPoint";

    public NoahPoint(int value) {
        super(value);
    }

}
