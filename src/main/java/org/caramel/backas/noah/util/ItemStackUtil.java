package org.caramel.backas.noah.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class ItemStackUtil {

    private ItemStackUtil() { throw new UnsupportedOperationException(); }


    @NotNull
    public static ItemStack get(
        final @NotNull Material material, final @Nullable Component displayName,
        final @Nullable Component... lore
    ) {
        return ItemStackUtil.modify(new ItemStack(material), displayName, List.of(lore), null);
    }

    @NotNull
    public static ItemStack get(
            final @NotNull Material material, final @Nullable Component displayName,
            final @Nullable Component lore, final @Nullable Consumer<? super ItemMeta> consumer
    ) {
        return ItemStackUtil.modify(new ItemStack(material), displayName, List.of(lore), consumer);
    }

    @NotNull
    public static ItemStack get(
        final @NotNull Material material, final @Nullable Component displayName,
        final @Nullable List<Component> lore, final @Nullable Consumer<? super ItemMeta> consumer
    ) {
        return ItemStackUtil.modify(new ItemStack(material), displayName, lore, consumer);
    }

    @NotNull
    @Deprecated
    public static ItemStack get(Material material, String displayName, List<String> lore) {
        ItemStack itemStack = new ItemStack(material);
        return getItemStack(itemStack, displayName, lore);
    }

    @NotNull
    public static ItemStack get(
        final @NotNull ItemStack stack, final @Nullable Component displayName,
        final @Nullable Component... lore
    ) {
        return ItemStackUtil.modify(stack, displayName, List.of(lore), null);
    }


    @NotNull
    public static ItemStack get(
        final @NotNull ItemStack stack, final @Nullable Component displayName,
        final @Nullable List<Component> lore
    ) {
        return ItemStackUtil.modify(stack, displayName, lore, null);
    }

    @NotNull
    public static ItemStack get(
        final @NotNull ItemStack stack, final @Nullable Component displayName,
        final @Nullable List<Component> lore, final @Nullable Consumer<? super ItemMeta> consumer
    ) {
        return ItemStackUtil.modify(stack, displayName, lore, consumer);
    }

    @NotNull
    @Deprecated
    public static ItemStack get(ItemStack itemStack, String displayName, List<String> lore) {
        return getItemStack(itemStack, displayName, lore);
    }

    @NotNull
    @Deprecated
    private static ItemStack getItemStack(ItemStack itemStack, String displayName, List<String> lore) {
        List<Component> coloredLore = new ArrayList<>();
        lore.forEach(s -> coloredLore.add(ColorString.parse(s)));
        return modify(itemStack, ColorString.parse(displayName), coloredLore, null);
    }

    @NotNull
    static ItemStack modify(
        final @NotNull ItemStack stack, final @Nullable Component displayName,
        final @Nullable List<Component> lore, final @Nullable Consumer<? super ItemMeta> consumer
    ) {
        stack.editMeta(meta -> {
            meta.displayName(displayName);
            meta.lore(lore);
            if (consumer != null) consumer.accept(meta);
        });
        return stack;
    }
}
