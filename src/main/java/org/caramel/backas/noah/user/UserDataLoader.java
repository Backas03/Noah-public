package org.caramel.backas.noah.user;

@FunctionalInterface
public interface UserDataLoader<R> {

    R apply(User user) throws Exception;

}
