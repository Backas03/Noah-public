package org.caramel.backas.noah.util;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class TimeUtil {

    public static String formatNow(String pattern) {
        return new SimpleDateFormat(pattern).format(Date.from(Instant.now()));
    }

    public static String formatNow() {
        return formatNow("yyyy년 MM월 dd일 HH:mm:ss");
    }

    public static String formatTime(int seconds) {
        return Math.floorDiv(seconds, 60) + ":" + String.format("%02d", seconds % 60);
    }
}
