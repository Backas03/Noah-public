package org.caramel.backas.noah.skin;

import kr.lostwar.fmj.api.weapon.Weapon;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.caramel.backas.noah.user.User;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

@Getter
@AllArgsConstructor
public class SkinEffect implements Listener {

    private final Skin skin;
    private final String id;
    private final int cost;

    private @NotNull @Setter ItemStack enabledIcon;
    private @NotNull @Setter ItemStack disabledIcon;
    private @NotNull @Setter ItemStack purchaseIcon;


    public SkinEffect(Skin skin, String id, int cost) {
        this.skin = skin;
        this.id = id;
        this.cost = cost;

        /* TODO: replace with resource */
        this.enabledIcon = new ItemStack(Material.BONE);
        this.disabledIcon = new ItemStack(Material.BONE_BLOCK);
        this.purchaseIcon = new ItemStack(Material.COBBLESTONE);
    }



    protected void hasEffectRun(Weapon weapon, User user, Runnable runnable) {
        Skin skin = Skin.find(weapon);
        if (skin != null) {
            skinData(user).thenApply(skinData -> {
                if (skinData.getCurrentSkin(skin.getOriginKey()).equals(weapon.getName())) {
                    hasEffect(user).thenApply(b -> {
                        if (b) runnable.run();
                        return b;
                    });
                }
                return skinData;
            });
        }
    }

    protected void hasEffectRunElse(User user, Runnable runnable, Runnable orElse) {
        hasEffect(user).thenApply(b -> {
            if (b == null || !b) orElse.run();
            else runnable.run();
            return b;
        });
    }

    protected void hasEffectRun(Weapon weapon, Player player, Runnable runnable) {
        hasEffectRun(weapon, User.get(player), runnable);
    }

    protected void hasEffectRunOrElse(Player player, Runnable runnable, Runnable orElse) {
        hasEffectRunElse(User.get(player),runnable, orElse);
    }

    protected CompletableFuture<Boolean> hasEffect(User user) {
        return skinData(user).thenApply(skinData -> skinData.hasSkin(skin) && skinData.hasEffect(skin, id) && Boolean.TRUE.equals(skinData.isEffectEnabled(skin, id)));
    }

    protected CompletableFuture<Boolean> hasEffect(Player player) {
        return hasEffect(User.get(player));
    }

    protected CompletableFuture<SkinData> skinData(User user) {
        return user.getDataContainer().getOrLoadAsync(SkinData.class);
    }

    protected CompletableFuture<SkinData> skinData(Player player) {
        return skinData(User.get(player));
    }
}
