package org.caramel.backas.noah.api.config.mapper;

import org.bukkit.boss.BarStyle;
import org.caramel.backas.noah.api.config.Config;

public class BarStyleMapper implements Config.Mapper<BarStyle> {

    @Override
    public Object set(BarStyle value) {
        return value.name();
    }

    @Override
    public BarStyle get(Object value) {
        if (value instanceof String) {
            try {
                return BarStyle.valueOf((String) value);
            } catch (IllegalArgumentException ignore) {

            }
        }
        return null;
    }

}
