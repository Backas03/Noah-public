package org.caramel.backas.noah.api.config.mapper;

import org.caramel.backas.noah.api.config.Config;

public class GunInventorySizeMapper implements Config.Mapper<Integer> {

    private final int weaponAmount;

    public GunInventorySizeMapper(int weaponAmount) {
        this.weaponAmount = weaponAmount;
    }

    @Override
    public Object set(Integer value) {
        if (value == null) return 1;
        return Math.max(Math.min(6, value), 0);
    }

    @Override
    public Integer get(Object value) {
        return (int) Math.ceil((double) weaponAmount / 9);
    }

}
