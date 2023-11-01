package org.caramel.backas.noah.user;

import org.bukkit.configuration.file.YamlConfiguration;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.level.Level;
import org.caramel.backas.noah.prefix.PrefixData;
import org.caramel.backas.noah.rating.TDMEloRating;
import org.caramel.backas.noah.skin.SkinData;
import org.caramel.backas.noah.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class UserDataContainer {

    public static final Set<Class<? extends UserData>> CACHED_LIST = Set.of(
            TDMEloRating.class,
            PrefixData.class,
            Level.class,
            SkinData.class
    );

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDataContainer.class);

    public static File getDataFolder() {
        return new File(Noah.getInstance().getDataFolder(), "UserData");
    }

    private final User user;

    public UserDataContainer(User user) {
        this.user = user;
    }

    private final Map<Class<?>, UserData> data = new HashMap<>();
    private Set<Class<?>> loading;

    public <T extends UserData> CompletableFuture<T> getOrLoadAsync(Class<T> clazz, UserDataLoader<T> loader) {
        if (!data.containsKey(clazz)) {
            if (loading == null) loading = new HashSet<>();
            if (!loading.contains(clazz)) {
                return CompletableFuture.supplyAsync(() -> {
                    loading.add(clazz); // loading start
                    // user.sendMessage(LOADING_START_MESSAGE);

                    /* store data */
                    T data = null;
                    try {
                        data = loader.apply(user);
                        this.data.put(data.getClass(), data);
                    } catch (Exception e) {
                        LoggerFactory.getLogger(clazz).error("유저 데이터 로딩 중 오류가 발생했습니다.", e);
                    } finally {
                        loading.remove(clazz); // loading end
                        if (loading.isEmpty()) loading = null;
                    }
                    return data;
                });
            }
            // user.sendMessage(LOADING_MESSAGE);
            return new CompletableFuture<>();
        }
        return CompletableFuture.supplyAsync(() -> get(clazz));
    }

    public <T extends UserData> CompletableFuture<T> getOrLoadAsync(Class<T> clazz) {
        if (!containsKey(clazz)) {
            if (loading == null) loading = new HashSet<>();
            if (!loading.contains(clazz)) {
                return CompletableFuture.supplyAsync(() -> {
                    loading.add(clazz); // loading start

                    // user.sendMessage(LOADING_START_MESSAGE);

                    /* store data */
                    T data = null;
                    try {
                        Method loaderMethod = clazz.getMethod("loader");
                        if (loaderMethod.invoke(null) instanceof UserDataLoader<?> loader) {
                            Object temp = loader.apply(user);
                            if (temp != null) {
                                data = clazz.cast(temp);
                                this.data.put(data.getClass(), data);
                            }
                        }
                    } catch (Exception e) {
                        LoggerFactory.getLogger(clazz).error("유저 데이터 로딩 중 오류가 발생했습니다.", e);
                    } finally {
                        loading.remove(clazz); // loading end
                        if (loading.isEmpty()) loading = null;
                    }
                    return data;
                });
            }
            // user.sendMessage(LOADING_MESSAGE);
            return new CompletableFuture<>();
        }
        return CompletableFuture.supplyAsync(() -> get(clazz));
    }

    public <T extends UserData> T getOrLoad(Class<T> clazz) {
        if (!containsKey(clazz)) {
            if (loading == null) loading = new HashSet<>();
            if (!loading.contains(clazz)) {
                loading.add(clazz); // loading start

                // user.sendMessage(LOADING_START_MESSAGE);

                /* store data */
                T data = null;
                try {
                    Method loaderMethod = clazz.getMethod("loader");
                    if (loaderMethod.invoke(null) instanceof UserDataLoader<?> loader) {
                        Object temp = loader.apply(user);
                        if (temp != null) {
                            data = clazz.cast(temp);
                            this.data.put(data.getClass(), data);
                        }
                    }
                } catch (Exception e) {
                    LoggerFactory.getLogger(clazz).error("유저 데이터 로딩 중 오류가 발생했습니다.", e);
                } finally {
                    loading.remove(clazz); // loading end
                    if (loading.isEmpty()) loading = null;
                }
                return data;
            }
        }
        return get(clazz);
    }

    public boolean containsKey(Class<? extends UserData> clazz) {
        return data.containsKey(clazz);
    }

    public <T extends UserData> T get(Class<T> clazz) {
        return data.containsKey(clazz) ? clazz.cast(data.get(clazz)) : null;
    }

    public void saveAll() throws IOException {
        File file = FileUtil.checkAndCreateFile(getFile());
        for (UserData userData : data.values()) {
            try {
                if (!userData.save(file)) {
                    LOGGER.warn("유저 데이터 저장 실패 {user={}, userData={}}", user, userData);
                }
            } catch (Exception e) {
                LOGGER.error(
                        "유저 데이터 저장 중 오류가 발생했습니다 {user=" + user + ", userData=" + userData + "}",
                        e
                );
            }
        }
    }

    public File getFile() {
        return new File(getDataFolder(), user.getUniqueId() + ".yml");
    }

    public YamlConfiguration loadYaml() throws IOException {
        return YamlConfiguration.loadConfiguration(FileUtil.checkAndCreateFile(getFile()));
    }
}
