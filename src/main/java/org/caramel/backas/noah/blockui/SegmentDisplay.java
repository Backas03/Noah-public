package org.caramel.backas.noah.blockui;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Lightable;
import org.caramel.backas.noah.util.Position;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@AllArgsConstructor
public class SegmentDisplay {

    @AllArgsConstructor
    private static class Pos {

        private int x;
        private int y;


        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Pos pos && pos.x == x && pos.y == y;
        }
    }

    public static final String WORLD_NAME = "lobby";

    public static final SegmentDisplay MINUTE_TEN = new SegmentDisplay(WORLD_NAME, new Position(-95, 61, 8));
    public static final SegmentDisplay MINUTE_ONE = new SegmentDisplay(WORLD_NAME, new Position(-95, 61, 4));
    public static final SegmentDisplay SECOND_TEN = new SegmentDisplay(WORLD_NAME, new Position(-95, 61, -2));
    public static final SegmentDisplay SECOND_ONE = new SegmentDisplay(WORLD_NAME, new Position(-95, 61, -6));

    public static void setTime(int seconds) {
        int value = Math.max(0, Math.min(5999, seconds)); // 00:00 ~ 99:59
        int secs = value % 60;
        int minutes = value / 60;
        MINUTE_TEN.set(minutes / 10);
        MINUTE_ONE.set(minutes % 10);
        SECOND_TEN.set(secs / 10);
        SECOND_ONE.set(secs % 10);

        /* on/off clock ":" */
        Location l1 = new Location(Bukkit.getWorld(WORLD_NAME), -95, 60, 0);
        Location l2 = new Location(Bukkit.getWorld(WORLD_NAME), -95, 58, 0);

        Block block1 = l1.getBlock();
        Block block2 = l2.getBlock();

        if (block1.getBlockData() instanceof Lightable b1 && block2.getBlockData() instanceof Lightable b2) {
            b1.setLit(!b1.isLit());
            b2.setLit(!b2.isLit());
            block1.setBlockData(b1);
            block2.setBlockData(b2);
        }

    }

    private String worldName;
    private Position position;

    public void set(int num) throws IllegalArgumentException {
        if (num < 0 || num > 9) throw new IllegalArgumentException("Segment Display can only explain a number between 0 and 9!");
        offAll();
        switch (num) {
            case 0 -> {
                setYAxis(0);
                on(1, 0);
                on(1, 4);
                setYAxis(2);
            }
            case 1 -> {
                setYAxis(2);
            }
            case 2 -> {
                setXAxis(0);
                on(2, 1);
                setXAxis(2);
                on(0, 3);
                setXAxis(4);
            }
            case 3 -> {
                setXAxis(0);
                on(2, 1);
                setXAxis(2);
                on(2, 3);
                setXAxis(4);
            }
            case 4 -> {
                on(0, 0);
                on(0, 1);
                on(0, 2);
                on(1, 2);
                setYAxis(2);
            }
            case 5 -> {
                setXAxis(0);
                on(0, 1);
                setXAxis(2);
                on(2, 3);
                setXAxis(4);
            }
            case 6 -> {
                setXAxis(0);
                setXAxis(2);
                setXAxis(4);
                on(0, 1);
                on(0, 3);
                on(2, 3);
            }
            case 7 -> {
                on(0, 1);
                on(0, 0);
                on(1, 0);
                on(2, 0);
                setYAxis(2);
            }
            case 8 -> {
                onAll();
            }
            case 9 -> {
                on(0, 0);
                on(0, 1);
                on(0, 2);
                on(1, 0);
                on(1, 2);
                setYAxis(2);
            }
        }
        apply();
    }

    private final Map<Pos, Boolean> data = new HashMap<>();

    private void apply() {
        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 5; y++) {
                    if (shouldIgnore(x, y)) continue;
                    Pos pos = new Pos(x, y);
                    boolean on = data.getOrDefault(pos, false);
                    Location location = position.toLocation(world).subtract(0, y, x);
                    Block block = location.getBlock();
                    if (block.getBlockData() instanceof Lightable lamp) {
                        lamp.setLit(on);
                        block.setBlockData(lamp);
                    }
                }
            }
        }
    }

    private void setXAxis(int y) {
        for (int x=0; x<3; x++) {
            if (shouldIgnore(x, y)) continue;
            data.put(new Pos(x, y), true);
        }
    }

    private void setYAxis(int x) {
        for (int y=0; y<5; y++) {
            if (shouldIgnore(x, y)) continue;
            Pos pos = new Pos(x, y);
            data.put(pos, true);
        }
    }

    private void set(int x, int y, boolean on) {
        if (shouldIgnore(x, y)) return;
        data.put(new Pos(x, y), on);
    }

    private void on(int x, int y) {
        set(x, y, true);
    }

    private void off(int x, int y) {
        set(x, y, false);
    }

    private void offAll() {
        data.clear();
    }

    private void onAll() {
        for (int x=0; x<3; x++) {
            for (int y=0; y<5; y++) {
                if (shouldIgnore(x, y)) continue;
                data.put(new Pos(x, y), true);
            }
        }
    }

    private boolean shouldIgnore(int x, int y) {
        return x == 1 && y % 2 == 1;
    }
}
