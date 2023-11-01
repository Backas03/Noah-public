package org.caramel.backas.noah;

import com.github.retrooper.packetevents.PacketEvents;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import kr.abins.noah.Range;
import kr.abins.noah.structure.classes.Practice;
import kr.lostwar.fmj.api.FMJ;
import kr.lostwar.fmj.api.events.WeaponBreakGlassEvent;
import kr.lostwar.fmj.api.events.WeaponDamageEntityEvent;
import moe.caramel.acacia.api.impl.inventory.AcaciaInventoryAPI;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.caramel.backas.noah.advancement.AdvancementListener;
import org.caramel.backas.noah.advancement.AdvancementManager;
import org.caramel.backas.noah.blockui.LampListener;
import org.caramel.backas.noah.command.*;
import org.caramel.backas.noah.deprecated.ResourceIds;
import org.caramel.backas.noah.game.GameLauncher;
import org.caramel.backas.noah.game.GameManager;
import org.caramel.backas.noah.game.ScoreboardManager;
import org.caramel.backas.noah.game.ocw.OCWTeamType;
import org.caramel.backas.noah.game.tdm.GameTDM;
import org.caramel.backas.noah.game.tdm.TDMGameLauncher;
import org.caramel.backas.noah.game.tdm.TDMGameMap;
import org.caramel.backas.noah.game.tdm.TDMGameMapRegistry;
import org.caramel.backas.noah.game.tdm.TDMTeam;
import org.caramel.backas.noah.kda.KDA;
import org.caramel.backas.noah.level.Level;
import org.caramel.backas.noah.noahpoint.NoahPoint;
import org.caramel.backas.noah.prefix.Prefix;
import org.caramel.backas.noah.prefix.PrefixData;
import org.caramel.backas.noah.rating.TDMEloRating;
import org.caramel.backas.noah.skin.Skin;
import org.caramel.backas.noah.skin.SkinData;
import org.caramel.backas.noah.skin.model.reaper.test.SkinReaperTest;
import org.caramel.backas.noah.tier.TierManager;
import org.caramel.backas.noah.ui.InteractableHologram;
import org.caramel.backas.noah.ui.gun.GunUIListener;
import org.caramel.backas.noah.user.User;
import org.caramel.backas.noah.user.UserListener;
import org.caramel.backas.noah.util.ItemStackUtil;
import org.caramel.backas.noah.util.Position;
import org.caramel.backas.noah.util.Region;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public final class Noah extends JavaPlugin implements Listener {

    private static Noah instance;

    public static Noah getInstance() {
        return instance;
    }

    private AdvancementManager advancementManager;

    public AdvancementManager getAdvancementManager() {
        return advancementManager;
    }


    @Override
    public void onEnable() {
        instance = this;

        initGameMaps();
        initGameLaunchers();
        GameManager.init();
        LoggerFactory.getLogger(this.getClass()).info("GAME MANAGER INITIALIZED!");

        registerCommands();
        Bukkit.getPluginManager().registerEvents(this, this);

        Bukkit.getPluginManager().registerEvents(new GunUIListener(), this);
        Bukkit.getPluginManager().registerEvents(new UserListener(), this);
        Bukkit.getPluginManager().registerEvents(new LampListener(), this);
        Bukkit.getPluginManager().registerEvents(new AdvancementListener(), this);
        Bukkit.getOnlinePlayers().forEach(User::init);
        AcaciaInventoryAPI.INSTANCE.activate(this);

        try {
            Prefix.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ScoreboardManager.init();

        registerClass();

        registerSkins();

        advancementManager = new AdvancementManager();
        advancementManager.init();

        User.initInstanceManager(this, 300); // 유저 데이터 인스턴스 관리 bukkit task (async) init

        PacketEvents.getAPI().getEventManager().registerListener(new InteractableHologram.PacketListener());

        Bukkit.getScheduler().runTask(this, () -> {
            final World world = new WorldCreator("range").environment(World.Environment.NORMAL).createWorld();

            Range.init(this);
            initRangeDummy(world);

            final Region region = new Region(new Position(-13, 23, 2), new Position(-17, 19, -2), Bukkit.getWorlds().get(0));
            Bukkit.getScheduler().runTaskTimer(this, () -> {
                region.getPlayers().forEach(player -> {
                    if (!GameManager.getInstance().isGameStarted() && GameManager.getInstance().isParticipated(player)) GameManager.getInstance().quit(player);
                    player.teleport(new Location(world, -23.5, 76.5, -21.5, 0, 0));
                });
            }, 0L, 20L);
        });

        // TODO: NoahLocalConnectionServer.start(3724);
    }

    private final Component dummyName = Component.text("스폰으로 돌아가기", NamedTextColor.GREEN);

    private void initRangeDummy(World world) {
        world.getEntitiesByClass(ArmorStand.class).forEach(Entity::remove);
        initDummy(new Location(world, -23.5, 66, 4.5, 0, 0));
        initDummy(new Location(world, -23.5, 66, -47.5, 180, 0));
        initDummy(new Location(world, -4.5, 76, -21.5, 90, 0));
        initDummy(new Location(world, -42.5, 76, -21.5, -90, 0));
    }

    private void initDummy(Location location) {
        ItemStack dummyItem = ItemStackUtil.get(new ItemStack(Material.STONE), 2);
        ArmorStand dummy = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        dummy.setItem(EquipmentSlot.HEAD, dummyItem);
        dummy.customName(dummyName);
        dummy.setCustomNameVisible(true);
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractAtEntityEvent event) {
        Component name = event.getRightClicked().customName();
        if (name != null && name.equals(dummyName)) {
            Lobby.teleportToSpawn(event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHealthGen(EntityRegainHealthEvent event) {
        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.MAGIC_REGEN) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (FMJ.getFMJPlayer(player).isHeldingWeapon()) return;
        if (
            player.isOp() &&
            player.getWorld().getName().equals("lobby") ||
            player.getWorld().getName().equals("range")
        ) return;
        event.setUseInteractedBlock(Event.Result.DENY);
    }

    @EventHandler
    public void onItemFrame(PlayerItemFrameChangeEvent event) {
        if (!event.getPlayer().isOp() && event.getAction() == PlayerItemFrameChangeEvent.ItemFrameChangeAction.ROTATE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onLeaveDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onGlassBrake(WeaponBreakGlassEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onDisable() {
        Practice.getListOfPractice().forEach(practice -> {
            practice.getDummies().forEach(dummy -> {
                ArmorStand armorStand = dummy.getArmorStand();
                if (armorStand != null) armorStand.remove();
            });
        });
        User.forceSaveAll();
        try {
            TierManager.save();
            Prefix.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // TODO: NoahLocalConnectionServer.shutdown();
    }


    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getOrigin().isFromFalling()) event.setCancelled(true);
    }

    @EventHandler
    public void onWeaponDamage(WeaponDamageEntityEvent event) {
        if (Lobby.getRegion().isInside(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    private void registerCommands() {
        Bukkit.registerCommand(this, "noah", new NoahMasterCommand());

        Bukkit.registerCommand(this, "join", new JoinCommand());
        Bukkit.registerCommand(this, "quit", new QuitCommand());
        Bukkit.registerCommand(this, "vote", new VoteCommand());
        Bukkit.registerCommand(this, "level", new LevelCommand());
        Bukkit.registerCommand(this, "skin", new SkinCommand());

        // Bukkit.registerCommand(this, "noah-point", new NoahPointCommand());
        Bukkit.registerCommand(this, "tier", new TierCommand());
        Bukkit.registerCommand(this, "kda", new KDACommand());
        Bukkit.registerCommand(this, "prefix", new PrefixCommand());

        Bukkit.registerCommand(this, "사격장나가기", new RangeExitCommand());
        Bukkit.registerCommand(this, "mmr", new RatingCommand());
    }

    private void registerClass() {
        ConfigurationSerialization.registerClass(Level.class);
        ConfigurationSerialization.registerClass(SkinData.class);
        ConfigurationSerialization.registerClass(NoahPoint.class);
        ConfigurationSerialization.registerClass(KDA.class);
        ConfigurationSerialization.registerClass(PrefixData.class);
        ConfigurationSerialization.registerClass(TDMEloRating.class);
    }

    private void initGameLaunchers() {
        GameLauncher.register(GameTDM.class, new TDMGameLauncher());
    }

    private void registerSkins() {
        Skin.register(new SkinReaperTest());
    }

    private void initGameMaps() {
        TDMGameMapRegistry.register(
                TDMGameMap.builder()
                        .setMapName("노아 상무")
                        .setWorldName("company")
                        .setIcon(ItemStackUtil.get(new ItemStack(Material.PAPER), ResourceIds.Model.NOAH_MAP_NOAHCOMPANY))
                        .setSpawn(TDMTeam.Type.RED, new Position(-49.5, 120.15, 24.5, 0, 0))
                        .setSpawn(TDMTeam.Type.BLACK, new Position(45.5, 120.15, -25.5, 0, 0))
                        .build()
        );
        TDMGameMapRegistry.register(
                TDMGameMap.builder()
                        .setMapName("파이톤")
                        .setWorldName("python")
                        .setIcon(ItemStackUtil.get(new ItemStack(Material.PAPER), ResourceIds.Model.NOAH_MAP_PYTHON))
                        .setSpawn(TDMTeam.Type.RED, new Position(1,81.15,64,-180,0))
                        .setSpawn(TDMTeam.Type.BLACK, new Position(1,81.15,-61,0,0))
                        .build()
        );
        TDMGameMapRegistry.register(
                TDMGameMap.builder()
                        .setMapName("리마")
                        .setWorldName("rima")
                        .setIcon(ItemStackUtil.get(new ItemStack(Material.PAPER), ResourceIds.Model.NOAH_MAP_RIMA))
                        .setSpawn(TDMTeam.Type.RED, new Position(-46,105.15,-7,-90,0))
                        .setSpawn(TDMTeam.Type.BLACK, new Position(68,105.15,17,90,0))
                        .build()
        );
        TDMGameMapRegistry.register(
                TDMGameMap.builder()
                        .setMapName("세인트 벨트")
                        .setWorldName("saint_belt")
                        .setIcon(ItemStackUtil.get(new ItemStack(Material.PAPER), ResourceIds.Model.NOAH_MAP_SAINTBELT))
                        .setSpawn(TDMTeam.Type.RED, new Position(-8,41.15,-66,90,0))
                        .setSpawn(TDMTeam.Type.BLACK, new Position(-104,41.15,-56,-90,0))
                        .build()
        );
    }
}
