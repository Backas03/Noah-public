package org.caramel.backas.noah.ui;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTInt;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRotation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.caramel.backas.noah.Lobby;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.game.GameManager;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;

@Getter
public class InteractableHologram {

    public static final class Info {

        private static final Map<UUID, Integer> HOLDER = new HashMap<>();

        public static final Component DEFAULT = Component.text("클릭하여 게임에 참여합니다.", NamedTextColor.GREEN);
        public static final Vector3d POSITION = new Vector3d(-40, 24, 0.5);

        public static void updateOnStart() {
            Lobby.getWorld().getPlayers().forEach(player -> {
                Integer entityId = HOLDER.get(player.getUniqueId());
                if (entityId != null) {
                    InteractableHologram hologram = SPAWNED.get(entityId);
                    if (hologram != null) {
                        hologram.setName(Component.text("클릭하여 현재 진행중인 게임에 참여합니다", NamedTextColor.GREEN));
                    }
                }
            });
        }

        public static void updateOnOver() {
            Lobby.getWorld().getPlayers().forEach(player -> {
                Integer entityId = HOLDER.get(player.getUniqueId());
                if (entityId != null) {
                    InteractableHologram hologram = SPAWNED.get(entityId);
                    if (hologram != null) {
                        if (!GameManager.getInstance().hasJoined(player)) {
                            hologram.setName(Component.text("클릭하여 게임에 참여합니다.", NamedTextColor.GREEN));
                            return;
                        }
                        hologram.setName(Component.text("빠른 게임 시작 투표를 원하시면 SHIFT 클릭해주세요.", NamedTextColor.YELLOW));
                    }
                }
            });
        }

        public static void updateOnJoin(Player player) {
            Integer entityId = HOLDER.get(player.getUniqueId());
            if (entityId != null) {
                InteractableHologram hologram = SPAWNED.get(entityId);
                if (hologram != null && !GameManager.getInstance().isGameStarted()) {
                    hologram.setName(Component.text("빠른 게임 시작 투표를 원하시면 SHIFT 클릭해주세요.", NamedTextColor.YELLOW));
                }
            }
        }

        public static void updateOnQuit(Player player) {
            Integer entityId = HOLDER.get(player.getUniqueId());
            if (entityId != null) {
                InteractableHologram hologram = SPAWNED.get(entityId);
                if (hologram != null) {
                    hologram.setName(Component.text("클릭하여 게임에 참여합니다.", NamedTextColor.GREEN));
                }
            }
        }

        public static void updateOnVote(Player player) {
            Integer entityId = HOLDER.get(player.getUniqueId());
            if (entityId != null) {
                InteractableHologram hologram = SPAWNED.get(entityId);
                if (hologram != null) {
                    hologram.setName(Component.text("빠른게임 시작 투표에 찬성하셨습니다.", NamedTextColor.RED));
                }
            }
        }

        public static void create(Player player) {
            int entityId = InteractableHologram.create(POSITION, (hologram, event) -> {
                if (GameManager.getInstance().isGameStarted()) {
                    GameManager.getInstance().join(player);
                    return;
                }
                if (GameManager.getInstance().hasJoined(player)) {
                    if (player.isSneaking() && !GameManager.getInstance().hasVoted(player)) {
                        GameManager.getInstance().vote(player);
                        return;
                    }
                    if (!player.isSneaking()) {
                        GameManager.getInstance().quit(player);
                        return;
                    }
                    return;
                }
                GameManager.getInstance().join(player);
            }, DEFAULT, player).getEntityId();

            HOLDER.put(player.getUniqueId(), entityId);
        }


        public static void spawn(Player player) {
            Integer entityId = HOLDER.get(player.getUniqueId());
            if (entityId != null) {
                InteractableHologram.spawn(entityId, player, DEFAULT);
            }
        }

        public static void remove(Player player) {
            Integer entityId = HOLDER.get(player.getUniqueId());
            if (entityId != null) {
                InteractableHologram.remove(entityId);
                HOLDER.remove(player.getUniqueId());
            }
        }


        public static boolean hasCreated(Player player) {
            Integer entityId = HOLDER.get(player.getUniqueId());
            if (entityId != null) {
                return InteractableHologram.hasCreated(entityId);
            }
            return false;
        }
    }

    // key: entityId, value: data
    private static final Map<Integer, InteractableHologram> SPAWNED = new HashMap<>();

    public static InteractableHologram create(Vector3d pos, BiConsumer<InteractableHologram, WrapperPlayClientInteractEntity> handler, Component name, @NotNull Player player) {
        InteractableHologram hologram = new InteractableHologram(player.getUniqueId(), generateEntityId(player), pos, handler);
        SPAWNED.put(hologram.getEntityId(), hologram);
        spawn(hologram.getEntityId(), player, name);
        return hologram;
    }

    public static void spawn(int entityId, Player player, Component name) {
        InteractableHologram hologram = SPAWNED.get(entityId);
        if (hologram != null) {
            PlayerManager playerManager = PacketEvents.getAPI().getPlayerManager();
            WrapperPlayServerSpawnEntity spawn = hologram.wrapper();

            WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata(spawn.getEntityId(), Arrays.asList(
                /* default value 0x00, invisible bit mask 0x20.
                index 0 의 value 수정 시 주의!!!
                https://wiki.vg/Entity_metadata#Entity
                    new EntityData(0, EntityDataTypes.BYTE, (byte) 0x20), */
                    new EntityData(2, EntityDataTypes.OPTIONAL_COMPONENT, Optional.of(GsonComponentSerializer.gson().serialize(name))),
                    new EntityData(3, EntityDataTypes.BOOLEAN, true), // no gravity
                    new EntityData(5, EntityDataTypes.BOOLEAN, true) // custom name visible
            ));

            NBTCompound customModelData = new NBTCompound();
            customModelData.setTag("CustomModelData", new NBTInt(2));
            ItemStack dummy = ItemStack.builder()
                    .nbt(customModelData)
                    .type(ItemTypes.STONE)
                    .amount(1)
                    .build();
            WrapperPlayServerEntityEquipment equipment = new WrapperPlayServerEntityEquipment(spawn.getEntityId(), List.of(new Equipment(EquipmentSlot.HELMET, dummy)));

            WrapperPlayServerEntityRotation rotation = new WrapperPlayServerEntityRotation(spawn.getEntityId(), -90, 0, true);

            playerManager.sendPacket(player, spawn);
            playerManager.sendPacket(player, rotation);
            playerManager.sendPacket(player, metadata);
            playerManager.sendPacket(player, equipment);

        }
    }

    public static boolean hasCreated(int entityId) {
        return SPAWNED.containsKey(entityId);
    }

    public static void remove(int entityId) {
        InteractableHologram hologram = SPAWNED.get(entityId);
        if (hologram != null) {
            hologram.remove();
        }
    }

    private static int generateEntityId(Player player) {
        return -player.getEntityId();
    }

    private final UUID ownerUUID;
    private final int entityId;
    private final Vector3d pos;
    private final UUID uuid;
    private final BiConsumer<InteractableHologram, WrapperPlayClientInteractEntity> handler;

    private InteractableHologram(UUID ownerUUID, int entityId, Vector3d pos, BiConsumer<InteractableHologram, WrapperPlayClientInteractEntity> handler) {
        this.ownerUUID = ownerUUID;
        this.entityId = entityId;
        this.pos = pos;
        this.uuid = UUID.randomUUID();
        this.handler = handler;
    }

    public void setName(Component name) {

        WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata(
                entityId,
                List.of(new EntityData(2, EntityDataTypes.OPTIONAL_COMPONENT, Optional.of(GsonComponentSerializer.gson().serialize(name))))
        );

        Player player = Bukkit.getPlayer(ownerUUID);
        if (player != null) PacketEvents.getAPI().getPlayerManager().sendPacket(player, metadata);
    }

    public void remove() {
        Player player = Bukkit.getPlayer(ownerUUID);
        if (player != null) {
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerDestroyEntities(entityId));
        }
        SPAWNED.remove(entityId);
    }

    private WrapperPlayServerSpawnEntity wrapper() {
        return new WrapperPlayServerSpawnEntity(
                entityId,
                Optional.of(this.uuid),
                EntityTypes.ARMOR_STAND,
                pos,
                0f,
                0f,
                0f,
                0,
                Optional.of(Vector3d.zero())
        );
    }

    public static class PacketListener extends PacketListenerAbstract {

        private final Set<UUID> rateLimit = new HashSet<>();

        @Override
        public void onPacketReceive(PacketReceiveEvent event) {
            if (event.getPacketType() != PacketType.Play.Client.INTERACT_ENTITY) return;
            final WrapperPlayClientInteractEntity wrapper = new WrapperPlayClientInteractEntity(event);

            InteractableHologram hologram = SPAWNED.get(wrapper.getEntityId());
            if (hologram != null) {
                UUID uuid = event.getUser().getUUID();
                if (rateLimit.contains(uuid)) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) player.sendActionBar(Component.text("입력이 너무 빠릅니다!", NamedTextColor.RED));
                    return;
                }
                rateLimit.add(uuid);
                Bukkit.getScheduler().runTaskLater(Noah.getInstance(), () -> {
                    rateLimit.remove(uuid);
                }, 20 * 2L);
                Bukkit.getScheduler().runTask(Noah.getInstance(), () -> {
                    hologram.handler.accept(hologram, wrapper);
                });
            }
        }
    }
}
