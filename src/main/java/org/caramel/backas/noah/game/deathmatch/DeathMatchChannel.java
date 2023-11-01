package org.caramel.backas.noah.game.deathmatch;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlaySendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
import kr.lostwar.fmj.api.FMJ;
import kr.lostwar.fmj.api.player.FMJPlayer;
import kr.lostwar.fmj.api.weapon.Weapon;
import lombok.Getter;
import moe.caramel.daydream.network.packet.PlayerInfoPacket;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.api.game.AbstractGame;
import org.caramel.backas.noah.api.game.GameChannel;
import org.caramel.backas.noah.api.game.IGameTeam;
import org.caramel.backas.noah.api.party.Party;
import org.caramel.backas.noah.api.user.User;
import org.caramel.backas.noah.api.util.KillLogger;
import org.caramel.backas.noah.resource.DeathMatchResource;
import org.caramel.backas.noah.util.ChannelUtil;
import org.caramel.backas.noah.util.ColorString;
import org.caramel.backas.noah.util.GameUtil;
import org.caramel.backas.noah.util.TimeUtil;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter
public abstract class DeathMatchChannel<T extends DeathMatchGameMap> extends GameChannel<T> {

    private final DeathMatchGame game;
    private boolean start;
    private int time;
    private Set<BukkitTask> tasks;
    private Set<UUID> invincible;
    private Scoreboard scoreboard;
    private Inventory gunInventory;
    private SimplePacketListenerAbstract tabAdapter;
    private BossBar gameBar;
    private Map<UUID, QuitData> quitLocation;
    private Map<UUID, DeathMatchParticipant> participants;

    public DeathMatchChannel(AbstractGame game, T gameMap) {
        super(game, gameMap);
        this.game = (DeathMatchGame) game;
        start = false;
    }

    @Override
    protected void onStart(AbstractGame game, Map<Party, IGameTeam> players) {
        if (!canStart()) {
            // TODO log to console
            return;
        }
        start = true;
        T gameMap = getGameMap();
        gameMap.loadMap();
        initVariables();
        time = 5;
        for (Party party : getPlayers().keySet()) {
            party.getAllMembers().forEach(user -> ChannelUtil.removeItem(user.toPlayer()));
        }
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(Noah.getInstance(), () -> {
            for (Map.Entry<Party, IGameTeam> entry : getPlayers().entrySet()) {
                TextColor color = entry.getValue() instanceof DeathMatchTeamType teamType ? teamType.getTextColor() : NamedTextColor.WHITE;
                entry.getKey().sendTitle(Title.title(
                    Component.text().append(
                        Component.text("게임 시작까지 ", NamedTextColor.GRAY),
                        Component.text(time + "초", NamedTextColor.GREEN)
                    ).build(),
                    Component.text().append(
                        Component.text("당신의 팀은 ", NamedTextColor.GRAY),
                        Component.text(entry.getValue().getName(), color),
                        Component.text(" 입니다", NamedTextColor.GRAY)
                    ).build(),
                    Title.Times.times(Duration.ofMillis(0), Duration.ofMillis(2000), Duration.ofMillis(0))
                ));
            }
            time--;
        }, 0L, 20L);
        Bukkit.getScheduler().runTaskLater(Noah.getInstance(), () -> {
            task.cancel();
            time = DeathMatchChannel.this.game.getConfig().getTime().get();
            Map<UUID, DeathMatchParticipant> participants = new HashMap<>();
            for (Map.Entry<Party, IGameTeam> entry : getPlayers().entrySet()) {
                DeathMatchTeamType teamType = null;
                try {
                   teamType = (DeathMatchTeamType) entry.getValue();
                } finally {
                    if (teamType == null) {
                        over();
                        Noah.getInstance().getDebugLogger().error("팀 타입 \"" + entry.getValue() + "\" (을)를 찾을 수 없어 게임이 강제 종료 되었습니다.");
                        for (Party party : getPlayers().keySet()) {
                            party.getAllMembers().forEach(user -> user.setGameData(null));
                        }
                    } else {
                        for (User user : entry.getKey().getAllMembers()) {
                            Player player = user.toPlayer();
                            if (player != null) player.setGameMode(GameMode.ADVENTURE);
                            DeathMatchParticipant participant = new DeathMatchParticipant(user.getUniqueId(), teamType);
                            participants.put(user.getUniqueId(), participant);
                        }
                    }
                }
            }
            this.participants = participants;
            initGameBar(participants.values());
            initScoreBoard(participants.values());
            for (DeathMatchParticipant participant : participants.values()) {
                CompletableFuture<Boolean> future = participant
                        .getUser()
                        .teleportAsync(getGameMap().getSpawn(participant.getTeam()));
                if (future == null) continue;
                future.thenApply(b -> {
                    Player player = participant.getUser().toPlayer();
                    if (player != null) {
                        player.setMaxHealth(100);
                        player.setHealth(100);
                        player.setHealthScale(20);
                        player.openInventory(getGunInventory());
                    }
                    return b;
                });
            }
            initTabList(participants);
            sendInfo();
        }, 100L);
    }

    public void sendMessage(TextComponent textComponent) {
        participants.values().forEach(participant -> participant.getUser().sendMessage(textComponent));
    }

    private void initVariables() {
        if (tasks != null) tasks.forEach(BukkitTask::cancel);
        if (gameBar != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.hideBossBar(gameBar);
            }
        }
        tasks = new HashSet<>();
        invincible = new HashSet<>();
        quitLocation = new HashMap<>();
        participants = new HashMap<>();
    }

    private void clearVariables() {
        if (tasks != null) tasks.forEach(BukkitTask::cancel);
        if (gameBar != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.hideBossBar(gameBar);
            }
        }
        tasks = null;
        invincible = null;
        quitLocation = null;
        participants = null;
        gameBar = null;
        scoreboard = null;
        gunInventory = null;
        tabAdapter = null;
        players = null;
    }

    @Override
    protected void onOver(AbstractGame game, Map<Party, IGameTeam> players) {
        for (BukkitTask timer : tasks) timer.cancel();
        int kr = getKills(DeathMatchTeamType.RED);
        int dr = getDeaths(DeathMatchTeamType.RED);
        int ar = getAssists(DeathMatchTeamType.RED);
        int kb = getKills(DeathMatchTeamType.BLACK);
        int db = getDeaths(DeathMatchTeamType.BLACK);
        int ab = getAssists(DeathMatchTeamType.BLACK);
        DeathMatchTeamType winner = kr == kb ? null : kr > kb ? DeathMatchTeamType.RED : DeathMatchTeamType.BLACK;
        String win = winner != null ? "<" + winner.getColor() + ">" + winner.getName() + " <green>의 승리!" : "<green>승리 팀 <white>: <yellow>무승부";
        broadcast("<gray>┌─────────────────────────┐");
        broadcast("  " + win);
        broadcast("          <gray>K/D/A");
        for (DeathMatchParticipant p : participants.values()) {
            p.getUser().sendMessage(
                    "  <yellow>내 전적 <gray>" + p.getKills() + "/" + p.getDeaths() + "/" + p.getAssists() + " <red>(" + String.format("%.2f", p.getTotalDamageDealt()) + ")"
            );
        }
        broadcast("");
        broadcast("  <red>RED <gray>" + kr + "/" + dr + "/" + ar + " <red>(" + String.format("%.2f", getDamageDealt(DeathMatchTeamType.RED)) + " Damage Dealt)");
        broadcast("  <dark_gray>BLACK <gray>" + kb + "/" + db + "/" + ab + " <red>(" + String.format("%.2f", getDamageDealt(DeathMatchTeamType.BLACK)) + " Damage Dealt)");
        broadcast("");
        broadcast("<gray>└─────────────────────────┘");

        PacketEvents.getAPI().getEventManager().unregisterListener(tabAdapter);

        final PlayerInfoPacket packet = Bukkit.createPacket(PlayerInfoPacket.class);
        List<PlayerInfoPacket.PlayerData> ps = new ArrayList<>();
        for (final Player player : Bukkit.getOnlinePlayers()) {
            ps.add(PlayerInfoPacket.addPlayer(player.getProfile(), player.getPing(), player.getGameMode(), player.displayName()));
        }
        packet.create(PlayerInfoPacket.Action.ADD_PLAYER, ps);

        for (DeathMatchParticipant p : participants.values()) {
            Player player = p.getUser().toPlayer();
            if (player != null) {
                player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                // player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
                player.getInventory().clear();
                player.sendPacket(packet);
                FMJ.getFMJPlayer(player).unsafeStatusReload();
            }
        }

        Bukkit.getScheduler().runTaskLater(Noah.getInstance(), () -> {
            for (Map.Entry<Party, IGameTeam> entry : getPlayers().entrySet()) {
                for (User user : entry.getKey().getAllMembers()) {
                    Player player = user.toPlayer();
                    if (player != null) {
                        player.teleport(Noah.getLobby().getSpawn());
                        player.setHealth(player.getMaxHealth());
                        ChannelUtil.giveItem(player); // 채널 아이템 지급
                    }
                }
            }
            Bukkit.getScheduler().runTaskLater(Noah.getInstance(), () -> {
                getGameMap().unloadMap();
                clearVariables();
                start = false;
            }, 5L);
        }, 100L);
    }


    public Set<DeathMatchParticipant> getParticipants(DeathMatchTeamType teamType) {
        Set<DeathMatchParticipant> ps = new HashSet<>();
        for (DeathMatchParticipant participant : participants.values()) {
            if (participant.getTeam() == teamType) {
                ps.add(participant);
            }
        }
        return ps;
    }

    private void initGameBar(Collection<DeathMatchParticipant> participants) {
        gameBar = BossBar.bossBar(getGameBarTitle(), 0.5F, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
        for (DeathMatchParticipant participant : participants) {
            Player player = participant.getUser().toPlayer();
            if (player != null) {
                player.showBossBar(gameBar);
            }
        }
        initGameBarTimer();
    }

    private void initScoreBoard(Collection<DeathMatchParticipant> participants) {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        for (DeathMatchTeamType teamType : DeathMatchTeamType.values()) {
            Team team = scoreboard.registerNewTeam(teamType.getName());
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
            team.color(NamedTextColor.GREEN);
        }

        for (DeathMatchParticipant participant : participants) {
            Team team = scoreboard.getTeam(participant.getTeam().getName());
            OfflinePlayer ofp = participant.getUser().toOfflinePlayer();
            team.addPlayer(ofp);
            Player player = participant.getUser().toPlayer();
            if (player != null) player.setScoreboard(scoreboard);
        }
    }

    private void initGameBarTimer() {
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(Noah.getInstance(), () -> {
            time--;
            updateGameBar();
        }, 20L, 20L);
        BukkitTask cancelTask = Bukkit.getScheduler().runTaskLater(Noah.getInstance(), () -> {
            task.cancel();
            tasks.remove(task);
            over();
        }, 20L * time);
        tasks.add(task);
        tasks.add(cancelTask);
    }

    private Component getGameBarTitle() {
        String color = time < 60 ? "<red>" : "<gray>";
        return ColorString.parse("<red>RED - <gray>" + getKills(DeathMatchTeamType.RED) + " | " + color + TimeUtil.formatTime(time) + "<gray> | " + getKills(DeathMatchTeamType.BLACK) + " <dark_gray>- BLACK");
    }

    private void updateGameBar() {
        gameBar.name(getGameBarTitle());
        int red = getKills(DeathMatchTeamType.RED);
        int black = getKills(DeathMatchTeamType.BLACK);
        int total = red + black;
        double progress = 0.5;
        if (red > black) progress += (double) red / total / 2;
        else if (red < black) progress -= (double) black / total / 2;
        gameBar.progress((float) (0.5 * Math.pow(0.5 * (2 * progress - 1), 3) + 0.5));
    }

    private void initTabList(Map<UUID, DeathMatchParticipant> participants) {
        final PlayerInfoPacket packet = createPlayerInfoPacket(participants.values());
        for (DeathMatchParticipant participant : participants.values()) {
            Player player = participant.getUser().toPlayer();
            final PlayerInfoPacket p = Bukkit.createPacket(PlayerInfoPacket.class);
            List<PlayerInfoPacket.PlayerData> playerInfoDataList = new ArrayList<>();
            for (Player player1 : Bukkit.getOnlinePlayers()) {
                if (!participants.containsKey(player1.getUniqueId())) {
                    playerInfoDataList.add(
                            PlayerInfoPacket.removePlayer(player1.getUniqueId())
                    );
                }
            }
            p.create(PlayerInfoPacket.Action.REMOVE_PLAYER, playerInfoDataList);
            if (player != null) {
                player.sendPacket(p);
                player.sendPacket(packet);
            }
        }
        tabAdapter = new SimplePacketListenerAbstract() {
            @Override
            public void onPacketPlaySend(PacketPlaySendEvent event) {
                if (event.getPacketType() != PacketType.Play.Server.PLAYER_INFO) return;
                if (!participants.containsKey(event.getUser().getUUID())) return;
                final WrapperPlayServerPlayerInfo playerInfo = new WrapperPlayServerPlayerInfo(event);
                if (playerInfo.getAction() == WrapperPlayServerPlayerInfo.Action.REMOVE_PLAYER) return; // TODO REMOVE_PLAYER 동작만 거르는게 맞는가? 이 코드가 의도한 로직을 이해할 수가 없음
                playerInfo.setAction(WrapperPlayServerPlayerInfo.Action.REMOVE_PLAYER);
                final List<WrapperPlayServerPlayerInfo.PlayerData> dataList = new ArrayList<>();
                for (WrapperPlayServerPlayerInfo.PlayerData data : playerInfo.getPlayerDataList()) {
                    if (!participants.containsKey(data.getUser().getUUID())) {
                        dataList.add(data);
                    }
                }
                playerInfo.setPlayerDataList(dataList);
                PacketEvents.getAPI().getProtocolManager().sendPacket(event.getChannel(), playerInfo);
                event.setCancelled(true);
                ((Player) event.getPlayer()).sendPacket(createPlayerInfoPacket(participants.values()));
            }
        };
        PacketEvents.getAPI().getEventManager().registerListener(tabAdapter);
    }

    private PlayerInfoPacket createPlayerInfoPacket(Collection<DeathMatchParticipant> participants) {
        final PlayerInfoPacket packet = Bukkit.createPacket(PlayerInfoPacket.class);
        Map<DeathMatchTeamType, LinkedList<DeathMatchParticipant>> ps = new LinkedHashMap<>();
        ps.put(DeathMatchTeamType.RED, new LinkedList<>());
        ps.put(DeathMatchTeamType.BLACK, new LinkedList<>());
        for (DeathMatchParticipant participant : participants) {
            ps.computeIfAbsent(participant.getTeam(), v -> new LinkedList<>()).add(participant);
        }
        LinkedList<PlayerInfoPacket.PlayerData> data = new LinkedList<>();
        for (DeathMatchTeamType teamType : DeathMatchTeamType.values()) {
            LinkedList<DeathMatchParticipant> participantLinkedList = ps.get(teamType);
            participantLinkedList.sort((p1, p2) -> Math.max(p1.getKills(), p2.getKills()));
            LinkedList<PlayerInfoPacket.PlayerData> section = new LinkedList<>();
            for (DeathMatchParticipant participant : participantLinkedList) {
                Player player = participant.getUser().toPlayer();
                if (player != null) {
                    section.add(
                        PlayerInfoPacket.addPlayer(
                            player.getProfile(),
                            player.getPing(),
                            player.getGameMode(),
                            Component.text().append(
                                Component.text("(", NamedTextColor.YELLOW),
                                Component.text(participant.getKills(), TextColor.fromHexString("#E65E00"), TextDecoration.BOLD),
                                Component.text(String.format("/%d/%d)", participant.getDeaths(), participant.getAssists()), NamedTextColor.YELLOW),
                                Component.text(participant.getUser().getName(), participant.getTeam().getTextColor())
                            ).build()
                        )
                    );
                }
            }
            data.addAll(section);
        }
        packet.create(PlayerInfoPacket.Action.ADD_PLAYER, data);
        return packet;
    }

    private void sendInfo() {
        for (DeathMatchParticipant participant : participants.values()) {
            participant.getUser().sendTitle("<green>게임 시작!", "<gray>제한 시간 내에 최대한 많은 적을 사살하세요", 0, 0, 30);
        }
        broadcast("<gray>┌─────────────────────────┐");
        broadcast("<red>* <white>" + getGame().getName() + " 게임이 시작되었습니다! <red>*");
        broadcast("<red>* <white>제한 시간 : <yellow>" + TimeUtil.formatTime(time) + " <red>*");
        broadcast("<red>* <white>맵 <gray>- " + getGameMap().getName() + " <red>*");
        broadcast("<yellow>게임 승리 조건");
        broadcast("<red>제한 시간 내 킬 수가 높은팀이 승리!");
        broadcast("<dark_gray>검정 팀 <gray>- " + getParticipants(DeathMatchTeamType.BLACK));
        broadcast("<red>레드 팀 <gray>- " + getParticipants(DeathMatchTeamType.RED));
        broadcast("<gray>└─────────────────────────┘");
    }

    public void onDamageEvent(Player attacker, Player victim, double damage) {
        DeathMatchParticipant pa = participants.get(attacker.getUniqueId());
        pa.addDamageDealt(victim.getUniqueId(), damage);
        pa.setTotalDamageDealt(pa.getTotalDamageDealt() + damage);
    }

    public void onDeathEvent(Player attacker, Player victim) {
        User uv = User.get(victim);
        DeathMatchParticipant pv2 = participants.get(uv.getUniqueId());
        pv2.setKillStreak(0);
        pv2.setGodMode(true);
        Location spawnPoint = getGameMap().getSpawn(pv2.getTeam());
        if (attacker == null) {
            respawn(victim, uv, spawnPoint, pv2);
            return;
        }
        User ua = User.get(attacker);
        GameChannel<?> c = ua.getGameData().getChannel();
        if (GameUtil.gameEquals(ua, DeathMatchGame.class) && GameUtil.gameEquals(ua, uv, true)) {
            DeathMatchParticipant pa = participants.get(ua.getUniqueId());
            DeathMatchParticipant pv = participants.get(uv.getUniqueId());
            pa.setKills(pa.getKills() + 1);
            pv.setDeaths(pv.getDeaths() + 1);
            for (DeathMatchParticipant participant : participants.values()) {
                if (participant.getTeam() != pv.getTeam()) {
                    if (!participant.equals(pa)) {
                        if (participant.getDamageDealt(pv.getUniqueId()) >= game.getConfig().getDamageToAssist().get()) {
                            participant.setAssists(participant.getAssists() + 1);
                        }
                    }
                    participant.getDamageDealt().remove(pv.getUniqueId());
                }
            }

            updateGameBar();

            String weaponName = "손";
            ItemStack itemInMainHand = attacker.getInventory().getItemInMainHand();
            if (itemInMainHand.getType() != Material.AIR) {
                String name = itemInMainHand.getItemMeta().getDisplayName();
                weaponName = name.isEmpty() ? itemInMainHand.getType().name() : name;
            }

            pa.setKillStreak(pa.getKillStreak() + 1);
            KillStreak killStreak = KillStreak.fromKills(pa.getKillStreak());

            KillLogger.display(c, DeathMatchResource.getKillLogMessage(game, pa, killStreak, pv, weaponName));

            attacker.playSound(attacker.getLocation(), "tdm.kill", 1, 1);

            victim.getInventory().clear();

            if (killStreak != null) {
                String killTitleFormat = "<white>" + game.getConfig().getKillStreakTitleFormat().get();
                String killSubTitleFormat = game.getConfig().getKillStreakSubTitleFormat().get();
                ua.sendTitle(
                        killTitleFormat.replaceAll("%kill_streak%", DeathMatchResource.getKillStreakTitleFormat(killStreak) + "<white>"),
                        killSubTitleFormat.replaceAll("%kill_streak%", DeathMatchResource.getKillStreakSubTitleFormat(killStreak) + "<white>"),
                        10, 10, 40
                );
            }
            final PlayerInfoPacket packet = createPlayerInfoPacket(getParticipants().values());
            for (DeathMatchParticipant participant : getParticipants().values()) {
                User user = participant.getUser();
                Player player = user.toPlayer();
                if (player != null) {
                    player.sendPacket(packet);
                }
            }

            victim.setHealth(victim.getMaxHealth());
            respawn(victim, uv, spawnPoint, pv);
        }
    }

    private void respawn(Player victim, User uv, Location spawnPoint, DeathMatchParticipant pv) {
        victim.teleportAsync(spawnPoint).thenApply(b -> {
            pv.setRespawnTime(game.getConfig().getRespawnCoolTime().get());
            victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 1));
            victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 9));
            BukkitTask task = Bukkit.getScheduler().runTaskTimer(Noah.getInstance(), () -> {
                pv.setRespawnTime(pv.getRespawnTime() - 1);
                String ft = game.getConfig().getRespawnTitleFormat().get();
                String fst = game.getConfig().getRespawnSubTitleFormat().get();
                String title = ft.replaceAll("%second%", String.valueOf(pv.getRespawnTime()));
                String subTitle = fst.replaceAll("%second%", String.valueOf(pv.getRespawnTime()));
                pv.getUser().sendTitle(title, subTitle, 0, 25, 0);
            }, 0L, 20L);
            tasks.add(task);
            BukkitTask cancelTask = Bukkit.getScheduler().runTaskLater(Noah.getInstance(), () -> {
                task.cancel();
                tasks.remove(task);
                victim.removePotionEffect(PotionEffectType.BLINDNESS);
                victim.removePotionEffect(PotionEffectType.SLOW);
                victim.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                try {
                    giveWeapon(victim);
                } finally {
                    uv.sendTitle(game.getConfig().getRespawnSuccessTitle().get(), game.getConfig().getRespawnSuccessSubTitle().get(), 0, 20 ,10);
                    BukkitTask t = Bukkit.getScheduler().runTaskLater(Noah.getInstance(), () -> {
                        uv.sendTitle(game.getConfig().getRespawnGodEndTitle().get(), game.getConfig().getRespawnGodEndSubTitle().get(), 0, 20 ,10);
                        pv.setGodMode(false);
                    }, 20L * game.getConfig().getRespawnGodDuration().get());
                    tasks.add(t);
                }
            }, 20L * game.getConfig().getRespawnCoolTime().get() + 1);
            tasks.add(cancelTask);
            tasks.removeIf(BukkitTask::isCancelled);
            return b;
        });
    }

    public void giveWeapon(Player player) {
        FMJPlayer fmjPlayer = FMJ.getFMJPlayer(player);
        fmjPlayer.resetWeaponData();
        Weapon mainWeapon = participants.get(player.getUniqueId()).getNextWeapon();
        if (fmjPlayer.getWeapon() != null) {
            fmjPlayer.setAmmo(mainWeapon.getReload().getAmount());
        }
        if (mainWeapon != null) {
            player.getInventory().setItem(0, mainWeapon.getItemStack());
        }
        Weapon subWeapon = FMJ.findWeapon(game.getConfig().getSubWeaponKey().get());
        if (subWeapon != null) player.getInventory().setItem(1, subWeapon.getItemStack());
        player.getInventory().setItem(3, DeathMatchResource.getHealKitItem());
        fmjPlayer.unsafeStatusReload(); // DEPRECATED
    }

    public void broadcast(String msg) {
        for (DeathMatchParticipant participant : participants.values()) {
            participant.getUser().sendMessage(msg);
        }
    }

    public int getKills(DeathMatchTeamType teamType) {
        int v = 0;
        for (DeathMatchParticipant p : participants.values()) {
            if (p.getTeam() == teamType) v += p.getKills();
        }
        return v;
    }

    public int getDeaths(DeathMatchTeamType teamType) {
        int v = 0;
        for (DeathMatchParticipant p : participants.values()) {
            if (p.getTeam() == teamType) v += p.getDeaths();
        }
        return v;
    }

    public int getAssists(DeathMatchTeamType teamType) {
        int v = 0;
        for (DeathMatchParticipant p : participants.values()) {
            if (p.getTeam() == teamType) v += p.getAssists();
        }
        return v;
    }

    public double getDamageDealt(DeathMatchTeamType teamType) {
        double v = 0;
        for (DeathMatchParticipant p : participants.values()) {
            if (p.getTeam() == teamType) v += p.getTotalDamageDealt();
        }
        return v;
    }

    public Inventory getGunInventory() {
        if (gunInventory == null) gunInventory = DeathMatchResource.createGunInventory(game);
        return gunInventory;
    }

    @Override
    public boolean canStart() {
        return !start;
    }

}
