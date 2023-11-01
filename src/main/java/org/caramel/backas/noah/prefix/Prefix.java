package org.caramel.backas.noah.prefix;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.util.FileUtil;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class Prefix {

    public static final Component NOAH = Component.text().append(
        Component.text("N", TextColor.color(0, 109, 119)),
        Component.text("O", TextColor.color(131, 197, 190)),
        Component.text("A", TextColor.color(237, 246, 249)),
        Component.text("H", TextColor.color(255, 221, 210))
    ).decorate(TextDecoration.ITALIC, TextDecoration.BOLD).build();

    public static final Component INFO_WITH_SPACE = Component.space().append(NOAH).append(
        Component.text(" + ", TextColor.fromHexString("#b5e48c"), TextDecoration.BOLD)
    );
    //⦁

    private static Map<String, Component> REGISTERED;

    public static void create(String namespacedKey, Component prefix) {
        REGISTERED.put(namespacedKey, prefix);
    }

    public static void delete(String namespacedKey) {
        REGISTERED.remove(namespacedKey);
    }

    public static Component getComponent(String namespacedKey) {
        return REGISTERED.get(namespacedKey);
    }

    public static Map<String, Component> getRegistered() {
        return REGISTERED;
    }

    public static boolean containsKey(String namespacedKey) {
        return REGISTERED.containsKey(namespacedKey);
    }

    public static void load() throws IOException {
        Map<String, Component> map = new HashMap<>();
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(FileUtil.checkAndCreateFile(getFile()));
        for (String namespacedKey : yaml.getKeys(false)) {
            String legacyText = yaml.getString(namespacedKey);
            if (legacyText == null) continue;
            Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(legacyText);
            map.put(namespacedKey, component);
        }
        REGISTERED = map;
    }

    public static void save() throws IOException {
        File file = FileUtil.checkAndCreateFile(getFile());
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        for (String key : yaml.getKeys(false)) { // 존재하지 않는 칭호 삭제
            if (REGISTERED.containsKey(key)) continue;
            yaml.set(key, null);
        }
        for (Map.Entry<String, Component> entry : REGISTERED.entrySet()) {
            String namespacedKey = entry.getKey();
            String legacyText = LegacyComponentSerializer.legacyAmpersand().serialize(entry.getValue());
            yaml.set(namespacedKey, legacyText);
        }
        yaml.save(file);
    }

    public static void sendPrefixGetAlart(Player player, String namespacedKey) {
        player.sendMessage(Component.text().append(
                Component.text("[칭호] ", NamedTextColor.GREEN),
                Prefix.getComponent(namespacedKey),
                Component.text(" 칭호를 획득하였습니다.", NamedTextColor.WHITE)
        ));
    }

    public static File getFile() {
        return new File(Noah.getInstance().getDataFolder(), "prefix.yml");
    }

    private Prefix() {
        throw new UnsupportedOperationException();
    }
}
