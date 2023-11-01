package org.caramel.backas.noah.api.config.mapper;

import org.caramel.backas.noah.api.config.Config;

import java.util.*;

public class SetMapper<V> implements Config.Mapper<Set<V>> {

    @Override
    public Object set(Set<V> value) {
        return new ArrayList<>(value);
    }

    @Override
    public Set<V> get(Object value) {
        if (value instanceof Collection) {
            return new HashSet<>((Collection<? extends V>) value);
        }
        return null;
    }

}
