package org.caramel.backas.noah.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import java.time.Duration;

public class TitleBuilder {

    public static TitleBuilder zeroInAndOut() {
        return new TitleBuilder()
                .setIn(0)
                .setOut(0);
    }

    private Component title;
    private Component sub;
    private long in;
    private long out;
    private long stay;

    public TitleBuilder() {
        title = Component.empty();
        sub = Component.empty();
        in = 2000;
        out = 2000;
        stay = 1000;
    }

    public TitleBuilder setIn(long millis) {
        in = millis;
        return this;
    }

    public TitleBuilder setOut(long millis) {
        out = millis;
        return this;
    }

    public TitleBuilder setStay(long millis) {
        stay = millis;
        return this;
    }

    public TitleBuilder setTitle(Component component) {
        title = component;
        return this;
    }

    public TitleBuilder setSubTitle(Component component) {
        sub = component;
        return this;
    }

    public Title build() {
        return Title.title(
                title,
                sub,
                Title.Times.times(
                        Duration.ofMillis(in),
                        Duration.ofMillis(stay),
                        Duration.ofMillis(out)
                )
        );
    }
}
