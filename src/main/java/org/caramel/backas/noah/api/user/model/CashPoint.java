package org.caramel.backas.noah.api.user.model;

import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("CashPoint")
public class CashPoint extends Point {

    public static final String KEY = "CashPoint";

    public CashPoint(int value) {
        super(value);
    }

}
