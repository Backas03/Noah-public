package org.caramel.backas.noah.api.config.mapper;

import org.bukkit.boss.BarColor;
import org.caramel.backas.noah.api.config.Config;

public class BarColorMapper implements Config.Mapper<BarColor> {

    @Override
    public Object set(BarColor value) {
        return value.name();
    }

    @Override
    public BarColor get(Object value) {
        if (value instanceof String) {
            try {
                return BarColor.valueOf((String) value);
            } catch (IllegalArgumentException ignore) {

            }
        }
        return null;
    }

}
