package org.caramel.backas.noah.tier;

import org.bukkit.configuration.file.YamlConfiguration;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.util.FileUtil;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class TierManager {

    static {
        try {
            data = YamlConfiguration.loadConfiguration(FileUtil.checkAndCreateFile(getFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static YamlConfiguration data;

    public static File getFile() {
        return new File(Noah.getInstance().getDataFolder(), "tiers.yml");
    }

    /*
    public static Tier getTier(UUID uuid) {
        int score = getScore(uuid);
        if (score >= 5000) return Tier.MERQURI;
        if (score >= 4800) return Tier.BENUS;
        if (score >= 4700) return Tier.MARS;
        if (score >= 4600) return Tier.JUPITER;
        if (score >= 4500) return Tier.NOAH;
        if (score >= 4400) return Tier.NEPTUNE;
        return null;
    }

     */

    public static int getScore(UUID uuid) {
        return data.getInt(uuid.toString(), 500);
    }

    public static void addScore(UUID uuid, int amount) {
        int score = getScore(uuid) + amount;
        data.set(uuid.toString(), score);
        /*
        for (int idx=0; idx<10; idx++) {
            if (TOPS[idx] == null) {
                TOPS[idx] = uuid;
                return;
            }
        }
        for (int i=0; i<10; i++) {
            for (int j=i+1; j<10; j++) {
                if (getScore(TOPS[i]) < getScore(TOPS[j-1])) {
                    UUID temp = TOPS[i];
                    TOPS[i] = TOPS[j-1];
                    TOPS[j-1] = temp;
                }
            }
        }
         */
    }

    /*
    public static final UUID[] TOPS = new UUID[10];

    public static Map<Integer, UUID> getKings() {
        Map<Integer, UUID> values = new HashMap<>();
        for (int i=1; i<10; i++) {
            values.put(i+1, TOPS[i]);
        }
        return values;
    }

    public static UUID getTheKing() {
        return TOPS[0];
    }

    public static boolean isKing(UUID uuid) {
        return getKings().containsValue(uuid);
    }

    public static int getKingRank(UUID uuid) {
        for (Map.Entry<Integer, UUID> entry : getKings().entrySet()) {
            if (entry.getValue().equals(uuid)) return entry.getKey();
        }
        return -1;
    }

    public static boolean isTheKing(UUID uuid) {
        return getTheKing() != null && getTheKing().equals(uuid);
    }
     */
    public static void save() throws IOException {
        data.save(FileUtil.checkAndCreateFile(getFile()));
    }
}
