package org.caramel.backas.noah.game.ocw;

import kr.lostwar.fmj.api.FMJ;
import kr.lostwar.fmj.api.events.WeaponDamageEntityEvent;
import kr.lostwar.fmj.api.player.FMJPlayer;
import kr.lostwar.fmj.api.weapon.Weapon;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.deprecated.ResourceIds;
import org.caramel.backas.noah.game.GameException;
import org.caramel.backas.noah.game.GameManager;
import org.caramel.backas.noah.game.IGame;
import org.caramel.backas.noah.game.ocw.agent.OCWAgent;
import org.caramel.backas.noah.game.ocw.agent.OCWPlayerAgent;
import org.caramel.backas.noah.game.ocw.inventory.AgentSelectInventory;
import org.caramel.backas.noah.game.tdm.TDMGameOption;
import org.caramel.backas.noah.game.tdm.TDMTeam;
import org.caramel.backas.noah.game.tdm.util.KillStreak;
import org.caramel.backas.noah.user.User;
import org.caramel.backas.noah.util.TitleBuilder;

import java.util.*;

public class GameOCW implements IGame, Listener {

    public static void initAgent(OCWParticipant participant) {
        updateTabListName(participant);
        participant.getUser().getPlayer().ifPresent(player -> {
            OCWPlayerAgent agent = participant.getAgent();
            if (agent != null) {
                OCWAgent info = agent.getInfo();

                player.setMaxHealth(info.getMaxHP());
                player.setHealth(player.getMaxHealth());

                Weapon weapon = FMJ.findWeapon(info.getWeaponKey());
                player.getInventory().setItem(0, weapon.getItemStack());
                FMJPlayer fmjPlayer = FMJ.getFMJPlayer(player);
                fmjPlayer.setAmmo(weapon.getReload().getAmount());
                fmjPlayer.unsafeStatusReload();
            }
        });
    }

    private static void updateTabListName(OCWParticipant participant) {
        OCWPlayerAgent playerAgent = participant.getAgent();
        if (playerAgent != null) {
            Component display = Component.text().append(
                    playerAgent.getInfo().getName(),
                    Component.space(),
                    Component.text("(", NamedTextColor.YELLOW),
                    Component.text(participant.kill, TextColor.color(230, 94, 0)),
                    Component.text(String.format("/%d/%d) ", participant.death, participant.assist), NamedTextColor.YELLOW),
                    Component.text(participant.getUser().getName(), participant.getTeamType().getColor())
            ).build();

            participant.getUser().getPlayer().ifPresent(player -> player.playerListName(display)); // update
        }
    }

    private final OCWGameMap map;
    private List<BukkitTask> tasks;
    private BossBar bossBar;
    private float time;
    private double gage;
    private Map<UUID, OCWTeamType> teamTypes;
    private Map<OCWTeamType, OCWTeam> teams;
    private Scoreboard scoreboard;

    public GameOCW(OCWGameMap map) {
        this.map = map;
        this.time = OCWGameOption.GAME_TIME;
    }

    @Override
    public void onStart(Set<User> players) throws GameException {
        tasks = new ArrayList<>();
        teamTypes = new HashMap<>();
        teams = new HashMap<>();

        initScoreboard();
        initTeam();
        initParticipants(players);

        teleportExistingParticipantsToSpawn();

        initTimer();
    }

    @Override
    public void onJoin(Player player) throws GameException {
        OCWParticipant participant = getParticipant(player);
        if (participant != null) {
            setScoreboard(player);
            setBossBar(player);
            teleportToSpawnAndSelectAgent(participant);
        }
    }

    @Override
    public void onQuit(Player player) throws GameException {

    }

    @Override
    public void onOver() {
        tasks.forEach(BukkitTask::cancel);
    }

    @Override
    public int getTime() {
        return (int) time;
    }

    public Set<OCWParticipant> getParticipants() {
        Set<OCWParticipant> participants = new HashSet<>();
        teams.values().forEach(team -> participants.addAll(team.getParticipants().values()));
        return participants;
    }

    public OCWTeamType getTeamType(Player player) {
        return getTeamType(player.getUniqueId());
    }

    public OCWTeamType getTeamType(UUID uuid) {
        return teamTypes.get(uuid);
    }

    public OCWTeam getTeam(UUID uuid) {
        return getTeam(getTeamType(uuid));
    }

    public OCWTeam getTeam(OCWTeamType type) {
        return teams.get(type);
    }

    public OCWParticipant getParticipant(UUID uuid) {
        return getTeam(getTeamType(uuid)).getParticipants().get(uuid);
    }

    public OCWParticipant getParticipant(Player player) {
        return getParticipant(player.getUniqueId());
    }

    public boolean isInGame(Player player) {
        return isInGameMap(player) && isParticipated(player.getUniqueId());
    }

    public boolean isParticipated(UUID uuid) {
        return getParticipant(uuid) != null;
    }

    public boolean isInGameMap(Player player) {
        return map.getWorld() == player.getWorld();
    }

    private void setBossBar(Player player) {
        player.showBossBar(bossBar);
    }

    private void setScoreboard(Player player) throws GameException {
        OCWTeamType teamType = getTeamType(player);
        if (teamType == null) {
            throw new GameException(Component.text("게임 팀을 찾을 수 없습니다. {player=" + player.getName() + "}"));
        }
        Team team = scoreboard.getTeam(teamType.name());
        if (team == null) {
            throw new GameException(Component.text("스코어보드 팀을 찾을 수 없습니다. {team=" + teamType.name() + "}"));
        }
        if (team.hasPlayer(player)) return;
        team.addPlayer(player);
        player.setScoreboard(scoreboard);
    }

    private void teleportToSpawnAndSelectAgent(OCWParticipant participant) throws GameException {
        Location location = map.getSpawnPoint(participant.getTeamType());
        if (location == null) {
            throw new GameException(Component.text("스폰 포인트를 찾을 수 없습니다. >> {teamType=" + participant.getTeamType() + "}", NamedTextColor.RED));
        }
        participant.getUser().getPlayer().ifPresent(player -> {
            player.teleport(location);
            AgentSelectInventory.open(participant);
        });
    }

    private void teleportExistingParticipantsToSpawn() throws GameException {
        for (OCWParticipant participant : getParticipants()) {
            teleportToSpawnAndSelectAgent(participant);
        }
    }

    private void initParticipants(Set<User> players) throws GameException {
        List<User> shuffled = new ArrayList<>(players);
        Collections.shuffle(shuffled);
        OCWTeamType[] teams = OCWTeamType.values();
        for (int i=0; i<shuffled.size(); i++) {
            User user = shuffled.get(i);
            OCWTeamType teamType = teams[i % teams.length];
            this.teamTypes.put(user.getUniqueId(), teamType);
            this.teams.get(teamType).initUserParticipant(user);

            Optional<Player> playerOptional = user.getPlayer();
            if (playerOptional.isPresent()) {
                setScoreboard(playerOptional.get());
            }
        }
    }

    private void initTeam() {
        for (OCWTeamType type : OCWTeamType.values()) {
            teams.put(type, new OCWTeam(type));
        }
    }

    private void initScoreboard() {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        for (OCWTeamType type : OCWTeamType.values()) {
            Team team = scoreboard.registerNewTeam(type.name());
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
            team.color(NamedTextColor.YELLOW);
        }
    }

    private void initTimer() {
        gage = 0;
        bossBar = BossBar.bossBar(Component.text("거점을 점령하세요!", NamedTextColor.GOLD), 0, BossBar.Color.PURPLE, BossBar.Overlay.PROGRESS);
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(Noah.getInstance(), () -> {
            if (time == 0) {
                try {
                    GameManager.getInstance().over();
                } catch (GameException e) {
                    e.printStackTrace();
                }
                return;
            }
            time = Math.max(0, time - (OCWGameOption.TICK_UPDATE_INTERVAL / 20f));
            int size_blue = 0;
            int size_pink = 0;
            for (Player inside : map.getRegion().getPlayers()) {
                if (getTeamType(inside) == OCWTeamType.BLUE) size_blue++;
                if (getTeamType(inside) == OCWTeamType.PINK) size_pink++;
            }
            updateBossbar(size_blue, size_pink);
        }, 0L, OCWGameOption.TICK_UPDATE_INTERVAL);

        tasks.add(task);
    }

    private void updateBossbar(int size_blue, int size_pink) {
        Component team;
        Component percentage;
        Component inside = Component.text().append(
                Component.text(" (", NamedTextColor.WHITE),
                Component.text(size_blue, OCWTeamType.BLUE.getColor()),
                Component.text(" : ", NamedTextColor.WHITE),
                Component.text(size_pink, OCWTeamType.PINK.getColor()),
                Component.text(")", NamedTextColor.WHITE)
        ).build();

        BossBar.Color color = bossBar.color();
        if (color == BossBar.Color.PURPLE) {
            color = size_blue > size_pink ? OCWTeamType.BLUE.getBarColor() : OCWTeamType.PINK.getBarColor();
        }

        int diff = Math.abs(size_blue - size_pink);
        double progressDiff = Math.round(1.5 * Math.log(diff + 1) / Math.log(2) * 100) / 100d / 100 * OCWGameOption.TICK_UPDATE_INTERVAL / 20; // log_2(diff) --> 소숫점 2자리 까지만

        String progressDiffString = String.format("%.2f", progressDiff * 100 / OCWGameOption.TICK_UPDATE_INTERVAL * 20); // 0 없앰

        /* 1 >= blue > 0 > pink >= -1 */

        if (size_blue > size_pink) { // 블루팀이 격돌중
            team = Component.text().append(
                    Component.text("현재", NamedTextColor.WHITE),
                    Component.text(" 파란팀", OCWTeamType.BLUE.getColor()),
                    Component.text("이 거점 점령중!", NamedTextColor.WHITE)
            ).build();
            if (bossBar.color() == OCWTeamType.BLUE.getBarColor()) { // +
                percentage = Component.text("(+" + progressDiffString + "%/초)", NamedTextColor.GREEN);
            } else {
                percentage = Component.text("(-" + progressDiffString + "%/초)", NamedTextColor.RED);
            }
            gage = Math.min(1, gage + progressDiff);
            if (gage >= 0) {
                color = OCWTeamType.BLUE.getBarColor();
            }
        } else if (size_pink == size_blue){
            team = Component.text("거점을 점령하세요!", NamedTextColor.GOLD);
            percentage = Component.text("(0%)", NamedTextColor.DARK_GRAY);
        } else { // 핑크팀이 격돌중
            team = Component.text().append(
                    Component.text("현재", NamedTextColor.WHITE),
                    Component.text(" 분홍팀", OCWTeamType.PINK.getColor()),
                    Component.text("이 거점 점령중!", NamedTextColor.WHITE)
            ).build();
            if (bossBar.color() == OCWTeamType.PINK.getBarColor()) { // +
                percentage = Component.text("(+" + progressDiffString + "%/초)", NamedTextColor.GREEN);
            } else {
                percentage = Component.text("(-" + progressDiffString + "%/초)", NamedTextColor.RED);
            }
            gage = Math.max(-1, gage - progressDiff);
            if (gage <= 0) {
                color = OCWTeamType.PINK.getBarColor();
            }
        }
        float progress = (float) Math.abs(gage);
        Component currentProgress = Component.text(String.format("%.2f%%", progress * 100),
                bossBar.color() == OCWTeamType.PINK.getBarColor() ?
                        OCWTeamType.PINK.getColor() :
                        OCWTeamType.BLUE.getColor()
        );
        Component title = Component.text().append(
                team,
                Component.space(),
                inside,
                Component.text(" | "),
                currentProgress,
                Component.space(),
                percentage
        ).build();

        bossBar.name(title);
        bossBar.color(color);
        bossBar.progress(progress);
    }

    @EventHandler
    public void onGunDamage(WeaponDamageEntityEvent event) {
        Player attacker = event.getPlayer();
        if (!(event.getVictim() instanceof Player victim)) return;
        if (!isInGame(attacker) || !isInGame(victim)) return;
        double damage = event.getDamage();
        Component cause = Component.text(event.getWeapon().getName());
        onDamage(attacker, victim, damage, cause, event);
    }

    private void onDamage(
            Player attacker,
            Player victim,
            double damage,
            Component cause,
            Cancellable event
    ) {
        OCWParticipant p_attacker = getParticipant(attacker);
        OCWParticipant p_victim = getParticipant(victim);

        if (p_attacker == null || p_victim == null) return;
        if (p_attacker.getTeamType() == p_victim.getTeamType()) {
            event.setCancelled(true);
            return;
        }
        if (p_victim.isInvincible() || p_victim.isRespawning()) {
            event.setCancelled(true);
            attacker.sendActionBar(Component.text(victim.getName() + "님은 현재 무적상태 입니다.", NamedTextColor.GRAY));
            return;
        }

        p_attacker.addDamageDealt(victim.getUniqueId(), damage);
        p_attacker.totalDamageDealt += damage;
        getTeam(p_attacker.getTeamType()).totalDamageDealt += damage;

        p_victim.lastDamagedParticipant = p_attacker;

        if (victim.getHealth() - damage <= 0) {
            event.setCancelled(true);
            double distance = attacker.getLocation().distance(victim.getLocation());
            onDeath(attacker, victim, distance, cause);
        }
    }

    private void onDeath(
            Player attacker,
            Player victim,
            double distance,
            Component cause
    ) {
        victim.getInventory().clear();
        victim.setHealth(victim.getMaxHealth());
        calculateDeath(attacker, victim);

        OCWParticipant p_attacker = getParticipant(attacker);
        OCWParticipant p_victim = getParticipant(victim);

        if (p_attacker == null || p_victim == null) return;

        updateTabListName(p_attacker);
        updateTabListName(p_victim);

        Component killStreakDisplay = Component.empty();
        KillStreak killStreak = KillStreak.fromKills(p_attacker.killStreak);
        if (killStreak != null) {
            Title title = new TitleBuilder()
                    .setSubTitle(Component.text(killStreak.getDisplay()))
                    .setIn(500)
                    .setStay(500)
                    .setOut(2000)
                    .build();
            attacker.showTitle(title);
            killStreakDisplay = Component.text(killStreak.getBarDisplay());
        }

        if (cause == null) {
            // TODO: killLogging
        }

        victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 1));
        victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 9));
        p_victim.setInvincible(true);
        p_victim.respawnTime = OCWGameOption.RESPAWN_TIME;
        BukkitTask timer = Bukkit.getScheduler().runTaskTimer(Noah.getInstance(), () -> {
            if (!isInGame(victim)) return;
            Title title = new TitleBuilder()
                    .setTitle(
                            Component.text().append(
                                    Component.text("리스폰 까지 ", NamedTextColor.RED),
                                    Component.text((p_victim.respawnTime--) + "초", NamedTextColor.GRAY)
                            ).build())
                    .setSubTitle(Component.text("F키 로 요원을 변경하실 수 있습니다", NamedTextColor.YELLOW))
                    .setIn(0)
                    .setOut(0)
                    .setStay(1250)
                    .build();
            victim.sendActionBar(Component.text(ResourceIds.Font.GAME_BLACK_SCREEN));
            victim.showTitle(title);
        }, 0L, 20L);

        BukkitTask later = Bukkit.getScheduler().runTaskLater(Noah.getInstance(), () -> {
            timer.cancel();
            tasks.remove(timer);

            if (!isInGame(victim)) return;

            victim.removePotionEffect(PotionEffectType.BLINDNESS);
            victim.removePotionEffect(PotionEffectType.SLOW);
            victim.closeInventory(InventoryCloseEvent.Reason.PLUGIN);

            initAgent(p_victim);
            BukkitTask invincibleTask = Bukkit.getScheduler().runTaskLater(Noah.getInstance(), () -> {
                p_victim.setInvincible(false);
                if (!isInGame(victim)) return;
                Title title = new TitleBuilder()
                        .setSubTitle(Component.text("무적 상태가 해제 되었습니다.", NamedTextColor.GRAY))
                        .setIn(0)
                        .setStay(1000)
                        .setOut(500)
                        .build();
                victim.showTitle(title);
            }, 20L * OCWGameOption.INVINCIBLE_DURATION);
            tasks.add(invincibleTask);
            Title title = new TitleBuilder()
                    .setTitle(Component.text("리스폰 완료!", NamedTextColor.GREEN))
                    .setSubTitle(Component.text(String.format("%d초의 무적시간이 적용됩니다.", OCWGameOption.INVINCIBLE_DURATION),
                            NamedTextColor.GRAY)
                    )
                    .setIn(0)
                    .setStay(1000)
                    .setOut(500)
                    .build();
            victim.sendActionBar(Component.empty());
            victim.showTitle(title);
        }, 20 * OCWGameOption.RESPAWN_TIME);
        tasks.add(timer);
        tasks.add(later);
        tasks.removeIf(BukkitTask::isCancelled);
    }

    private void calculateDeath(Player attacker, Player victim) {
        OCWParticipant p_attacker = getParticipant(attacker);
        OCWParticipant p_victim = getParticipant(victim);

        if (p_attacker == null || p_victim == null) return;

        OCWTeam attackerTeam = getTeam(p_attacker.getTeamType());

        p_attacker.kill++;
        p_attacker.killStreak++;

        attackerTeam.kills++;

        p_victim.death++;
        p_victim.killStreak = 0;

        for (OCWParticipant participant : attackerTeam.getParticipants().values()) {
            if (participant == p_attacker) continue;
            if (participant.getDamageDealt(victim.getUniqueId()) >= OCWGameOption.ASSIST_DAMAGE_THRESHOLD) {
                participant.assist++;
                attackerTeam.assists++;
            }
            participant.getDamageDealt().remove(victim.getUniqueId());
        }
    }
}
