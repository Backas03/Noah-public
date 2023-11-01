package org.caramel.backas.noah.level;

public class ExpData {

    public static final int WEIGHT = 100, MAX_LEVEL = 310;

    public static int getExperience(int level) {
        return level * level * WEIGHT;
    }

    public static int getLevel(int exp) {
        return Math.min((int) Math.floor(Math.sqrt((double) exp / WEIGHT)), MAX_LEVEL);
    }
}
