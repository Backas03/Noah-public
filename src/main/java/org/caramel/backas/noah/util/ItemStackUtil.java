package org.caramel.backas.noah.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.function.Consumer;

public final class ItemStackUtil {

    ItemStackUtil() { throw new UnsupportedOperationException(); }

    public static ItemStack get(ItemStack itemStack, int customModelData) {
        itemStack.editMeta(itemMeta -> {
            itemMeta.setCustomModelData(customModelData);
        });
        return itemStack;
    }

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
