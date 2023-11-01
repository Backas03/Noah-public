package org.caramel.backas.noah.game.tdm;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import kr.lostwar.fmj.api.FMJ;
import kr.lostwar.fmj.api.events.*;
import kr.lostwar.fmj.api.player.FMJPlayer;
import kr.lostwar.fmj.api.weapon.Weapon;
import moe.caramel.daydream.util.Pair;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.caramel.backas.noah.Noah;
import org.caramel.backas.noah.advancement.AdvancementConstant;
import org.caramel.backas.noah.advancement.AdvancementKeys;
import org.caramel.backas.noah.advancement.AdvancementManager;
import org.caramel.backas.noah.api.event.ParticipantKillEnemyEvent;
import org.caramel.backas.noah.api.event.TDMGameOverEvent;
import org.caramel.backas.noah.blockui.SegmentDisplay;
import org.caramel.backas.noah.deprecated.ResourceIds;
import org.caramel.backas.noah.game.GameException;
import org.caramel.backas.noah.game.GameManager;
import org.caramel.backas.noah.game.IGame;
import org.caramel.backas.noah.game.IGameMap;
import org.caramel.backas.noah.game.ScoreboardManager;
import org.caramel.backas.noah.game.tdm.inventory.TDMGunSelectInventory;
import org.caramel.backas.noah.game.tdm.util.JoinTimer;
import org.caramel.backas.noah.util.KillLogger;
import org.caramel.backas.noah.game.tdm.util.KillStreak;
import org.caramel.backas.noah.kda.KDA;
import org.caramel.backas.noah.level.LevelManager;
import org.caramel.backas.noah.prefix.Prefix;
import org.caramel.backas.noah.rating.TDMEloRating;
import org.caramel.backas.noah.rating.TDMEloUtil;
import org.caramel.backas.noah.tier.TierManager;
import org.caramel.backas.noah.user.User;
import org.caramel.backas.noah.util.*;
import org.caramel.backas.noah.Lobby;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class GameTDM implements IGame, Listener {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameTDM.class);

    private int time;
    private Scoreboard scoreboard;
    private BossBar bossBar;
    private Map<UUID, TDMTeam.Type> teamTypes;
    private Map<TDMTeam.Type, TDMTeam> teams;

    private final IGameMap map;
    private final Set<BukkitTask> tasks;

    public GameTDM(IGameMap map) {
        this.map = map;
        tasks = new HashSet<>();
    }

    private void updateTabListName(TDMParticipant participant) {
        participant.getUser().getPlayer().ifPresent(player -> {
            player.playerListName(
                    Component.text().append(
                            Component.text("(", NamedTextColor.YELLOW),
                            Component.text(participant.kill, TextColor.color(230, 94, 0)),
                            Component.text(String.format("/%d/%d) ", participant.death, participant.assist), NamedTextColor.YELLOW),
                            Component.text(player.getName(), participant.getTeamType() == TDMTeam.Type.RED ? TDMGameOption.RED_TEAM_COLOR : TDMGameOption.BLACK_TEAM_COLOR)
                    ).build()
            );
        });
    }

    @Override
    public void onStart(Set<User> players) {

        /* init scoreboard start */
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        for (TDMTeam.Type type : TDMTeam.Type.values()) {
            Team team = scoreboard.registerNewTeam(type.name());
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
            team.color(NamedTextColor.GREEN);
        }
        /* init scoreboard end */

        /* init teams start */
        teams = new HashMap<>();
        teamTypes = new HashMap<>();
        for (TDMTeam.Type type : TDMTeam.Type.values()) {
            teams.put(type, new TDMTeam(type));
        }
        /* init teams end */

        time = TDMGameOption.GAME_TIME;
        bossBar = BossBar.bossBar(getBossBarTitle(), 0.5f, BossBar.Color.RED, BossBar.Overlay.PROGRESS);

        /* init participant start */
        List<User> shuffled = new ArrayList<>(players);
        Collections.shuffle(shuffled);
        TDMTeam.Type[] teams = TDMTeam.Type.values();
        for (int i=0; i<shuffled.size(); i++) {
            User user = shuffled.get(i);
            TDMTeam.Type teamType = teams[i % teams.length];
            initParticipant(user, teamType);
        }
        /* init participant end */


        /* init game timer start */
        BukkitTask timer = Bukkit.getScheduler().runTaskTimer(Noah.getInstance(), () -> {
            time--;
            if (time > 0) {
                updateBossBar();
                SegmentDisplay.setTime(time);
                if (time <= 10 || time == 30 || time == 59) {
                    broadcast(Prefix.INFO_WITH_SPACE.append(Component.text("게임 종료까지 ").append(Component.text(time + "초", NamedTextColor.RED))));
                }
            }
            else if (time == 0) {
                try {
                    GameManager.getInstance().over();
                } catch (GameException e) {
                    Bukkit.broadcast(e.getComponent());
                    e.printStackTrace();
                }
            }
        }, 20L, 20L);
        tasks.add(timer);
        /* init game timer end */

        /* send starting message start */
        this.teams.values().forEach(team -> team.getParticipants().forEach(participant -> {
                User user = participant.getUser();
                user.getPlayer().ifPresent(player -> {
                    player.getInventory().clear();
                    player.showTitle(
                            new TitleBuilder()
                                    .setTitle(Component.text("게임 시작!", NamedTextColor.GREEN))
                                    .setSubTitle(Component.text("제한 시간 내에 최대한 많은 적을 사살하세요", NamedTextColor.GRAY))
                                    .setIn(0)
                                    .setOut(30)
                                    .setStay(0)
                                    .build()
                    );
                    player.sendMessage(Component.text("┌─────────────────────────┐", NamedTextColor.GRAY));
                    player.sendMessage(
                            Component.text("* ", NamedTextColor.RED).append(
                                    Component.text("팀 데스매치 게임이 시작되었습니다!", NamedTextColor.WHITE).append(
                                            Component.text(" *", NamedTextColor.RED)
                                    )
                            )
                    );
                    player.sendMessage(
                            Component.text("* ", NamedTextColor.RED).append(
                                    Component.text("제한 시간 : ", NamedTextColor.YELLOW).append(
                                            Component.text(TimeUtil.formatTime(TDMGameOption.GAME_TIME), NamedTextColor.YELLOW).append(
                                                    Component.text(" *", NamedTextColor.RED)
                                            )
                                    )
                            )
                    );
                    player.sendMessage(
                            Component.text("* ", NamedTextColor.RED).append(
                                    Component.text("맵 : ", NamedTextColor.WHITE).append(
                                            map.getName().append(
                                                    Component.text(" *", NamedTextColor.RED)
                                            )
                                    )
                            )
                    );
                    player.sendMessage(Component.text("게임 승리 조건", NamedTextColor.YELLOW));
                    player.sendMessage(Component.text("제한 시간 내 킬 수가 높은팀이 승리!", NamedTextColor.RED));
                    player.sendMessage(
                            Component.text("검정 팀 ", NamedTextColor.DARK_GRAY).append(
                                    Component.text(getTeam(TDMTeam.Type.BLACK).getParticipants().toString(), NamedTextColor.WHITE)
                            )
                    );
                    player.sendMessage(
                            Component.text("레드 팀 ", NamedTextColor.RED).append(
                                    Component.text(getTeam(TDMTeam.Type.RED).getParticipants().toString(), NamedTextColor.WHITE)
                            )
                    );
                    player.sendMessage(Component.text("└─────────────────────────┘", NamedTextColor.GRAY));
                });
            })
        );
        /* send starting message end */
    }

    private void initParticipant(User user, TDMTeam.Type teamType) {
        TDMTeam.Type currentTeamType = getTeamType(user.getUniqueId());
        TDMParticipant participant;
        if (currentTeamType == null) {
            participant = new TDMParticipant(user, teamType);
            getTeam(teamType).initParticipant(participant);
            teamTypes.put(user.getUniqueId(), teamType);
        } else participant = getTeam(currentTeamType).getParticipant(user.getUniqueId());
        participant.godMode = false;
        initViewAndTeleport(participant);
    }

    private void initViewAndTeleport(TDMParticipant participant) {
        participant.getUser().getPlayer().ifPresent(player -> {
            Location spawnPoint = map.getSpawnPoint(participant.getTeamType());
            if (spawnPoint == null) {
                LOGGER.warn("맵 스폰 포인트를 찾을 수 없습니다. map={}", map);
                return;
            }
            boolean success = player.teleport(spawnPoint);
            if (!success) {
                LOGGER.warn("게임 스폰 지점 텔레포트 실패. player={}, location={}", player.getName(), spawnPoint);
                return;
            }
            player.setMaxHealth(100);
            player.setHealth(100);
            player.setHealthScale(20);
            Objects.requireNonNull(scoreboard.getTeam(participant.getTeamType().name())).addPlayer(player);
            updateTabListName(participant);
            player.setScoreboard(scoreboard);
            player.showBossBar(bossBar);

            participant.nextWeapon = null; // 중참하였을때 총기가 지급 되도록 함.
            TDMGunSelectInventory.open(participant);
        });
    }

    private void broadcast(Component component) {
        this.teams.values().forEach(team -> team.getParticipants().forEach(participant -> {
            User user = participant.getUser();
            user.getPlayer().ifPresent(player -> player.sendMessage(component));
        }));
    }

    @Override
    public void onJoin(Player player) throws GameException {
        if (time <= TDMGameOption.MIN_JOIN_TIME) {
            throw new GameException(Component.text("게임 중도 참여 가능시간이 아닙니다."));
        }

        JoinTimer.start(player, () -> {

            TDMTeam.Type teamType = getTeamType(player);
            if (teamType == null) {
                int red = getTeam(TDMTeam.Type.RED).getParticipants().size();
                int black = getTeam(TDMTeam.Type.BLACK).getParticipants().size();
                if (red > black) teamType = TDMTeam.Type.BLACK;
                else teamType = TDMTeam.Type.RED;
            }

            initParticipant(User.get(player), teamType);
            broadcast(Component.text().append(
                    Prefix.INFO_WITH_SPACE,
                    Component.text(player.getName(), NamedTextColor.YELLOW),
                    Component.text(" 님께서 ", NamedTextColor.WHITE),
                    teamType.getName(),
                    Component.text(" 으로 참여했습니다.", NamedTextColor.WHITE)
            ).build());
        });
    }


    @Override
    public void onQuit(Player player) throws GameException {
        if (JoinTimer.RUNNING.containsKey(player.getUniqueId())) {
            throw new GameException(Prefix.INFO_WITH_SPACE.append(Component.text("게임 참여 대기중입니다. 퇴장이 불가능합니다.")));
        }
        if (getTeamType(player) == null) {
            throw new GameException(Prefix.INFO_WITH_SPACE.append(Component.text("게임에 참여중이지 않아 퇴장이 불가능합니다.")));
        }
        TDMParticipant participant = getTeam(getTeamType(player)).getParticipant(player.getUniqueId());
        if (getTeam(getTeamType(player)).getParticipant(player.getUniqueId()).isRespawning()) {
            throw new GameException(Prefix.INFO_WITH_SPACE.append(Component.text("리스폰 대기중에는 게임에서 떠나실 수 없습니다.")));
        }
        participant.godMode = false;
        player.hideBossBar(bossBar);
        player.closeInventory(InventoryCloseEvent.Reason.PLUGIN); // 플레이어 인벤토리 닫기 (총기 선택창일 경우를 방지)
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType())); // 플레이어 포션 효과 전부 제거
        player.getInventory().clear(); // 플레이어 인벤토리 클리어
        FMJ.getFMJPlayer(player).unsafeStatusReload(); // DEPRECATED
        Team team = scoreboard.getPlayerTeam(player);
        if (team != null) {
            team.removePlayer(player);
        }
        ScoreboardManager.activateScoreboard(player);

        Lobby.teleportToSpawn(player); // 스폰으로 텔포

        /* 플레이어 체력 원상복구 */
        player.resetMaxHealth();
        player.setHealth(player.getMaxHealth());

        /* 플레이어 탭 리스트 이름 원상복구 */
        PlayerListNameUtil.setDefault(player);

        ChannelItemUtil.giveItem(player);

        Player attacker = player.getKiller();
        if (attacker != null) {
            deathPlayer(attacker, player, null, true);
        }

        /* 플레이어 0명일시 강제종료 */
        if (getTeam(TDMTeam.Type.BLACK).insideEmpty() && getTeam(TDMTeam.Type.RED).insideEmpty()) {
            GameManager.getInstance().over();
        }
    }

    @Override
    public void onOver() {
        tasks.forEach(BukkitTask::cancel);
        JoinTimer.cancelAll();

        calculateTier();
        calculateKDA();


        if (map.getWorld() != null) {
            for (Player player : map.getWorld().getPlayers()) {
                player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                Lobby.teleportToSpawn(player);
            }
        }

        TDMTeam red = getTeam(TDMTeam.Type.RED);
        TDMTeam black = getTeam(TDMTeam.Type.BLACK);

        Component winMsg = null;
        TDMTeam win;
        if (red.kills > black.kills) win = red;
        else if (red.kills < black.kills) win = black;
        else {
            win = null;
            winMsg = Component.text().append(
                    Component.text("승리 팀 ", NamedTextColor.GREEN),
                    Component.text(": ", NamedTextColor.WHITE),
                    Component.text("무승부", NamedTextColor.YELLOW)
            ).build();
        }

        if (win != null) winMsg = win.getType().getName().append(Component.text(" 의 승리!", NamedTextColor.GREEN));

        broadcast(Component.text("┌─────────────────────────┐", NamedTextColor.DARK_GRAY));
        broadcast(Component.text("  ").append(winMsg));
        this.teams.values().forEach(team -> team.getParticipants().forEach(participant -> {
            User user = participant.getUser();
            if (user != null) {
                user.getPlayer().ifPresent(player -> {
                    player.sendMessage(
                            Component.text().append(
                                    Component.text("  내 전적 ", NamedTextColor.YELLOW),
                                    Component.text(String.format("%d/%d/%d ",
                                            participant.kill,
                                            participant.death,
                                            participant.assist), NamedTextColor.GRAY),
                                    Component.text(String.format("(%.2f)", participant.totalDamageDealt), NamedTextColor.RED)
                            )
                    );
                });
            }
        }));
        broadcast(Component.empty());
        broadcast(
                Component.text().append(
                        Component.text("  RED ", NamedTextColor.RED),
                        Component.text(String.format("  %d/%d/%d ", red.kills, black.kills, red.assists), NamedTextColor.GRAY),
                        Component.text(String.format(" (%.2f Damage Dealt)", red.totalDamageDealt), NamedTextColor.RED)
                ).build()
        );
        broadcast(
                Component.text().append(
                        Component.text("  BLACK ", NamedTextColor.DARK_GRAY),
                        Component.text(String.format("  %d/%d/%d ", black.kills, red.kills, black.assists), NamedTextColor.GRAY),
                        Component.text(String.format(" (%.2f Damage Dealt)", black.totalDamageDealt), NamedTextColor.RED)
                ).build()
        );
        TDMParticipant killMVP = null;
        TDMParticipant assistMVP = null;
        if (red.getParticipants().size() + black.getParticipants().size() >= 6) {

            for (TDMTeam.Type type : TDMTeam.Type.values()) {
                for (TDMParticipant p : getTeam(type).getParticipants()) {
                    if (p.getUser().getPlayer().isEmpty()) continue;
                    if (p.kill != 0) {
                        if (killMVP == null || killMVP.kill == p.kill && killMVP.totalDamageDealt < p.totalDamageDealt || killMVP.kill < p.kill) {
                            killMVP = p;
                        }
                    }
                    if (p.assist != 0) {
                        if (assistMVP == null || assistMVP.assist == p.assist && assistMVP.totalDamageDealt < p.totalDamageDealt || assistMVP.assist < p.assist) {
                            assistMVP = p;
                        }
                    }
                }
            }
            broadcast(Component.empty());
            if (killMVP != null) {
                broadcast(Component.text().append(
                        Component.text("    이번 게임의"),
                        Component.text(" Kill MVP", NamedTextColor.RED),
                        Component.text("는 "),
                        Component.text(killMVP.kill + "킬 (" + String.format("%.2f", killMVP.totalDamageDealt) + " DMG)", NamedTextColor.GOLD),
                        Component.text("을 한 ", NamedTextColor.WHITE),
                        Component.text(killMVP.getUser().getName(), NamedTextColor.YELLOW),
                        Component.text("님 입니다.", NamedTextColor.WHITE)
                ).build());
            }
            if (assistMVP != null) {
                broadcast(Component.text().append(
                        Component.text("    이번 게임의"),
                        Component.text(" Assist MVP", NamedTextColor.GREEN),
                        Component.text("는 "),
                        Component.text(assistMVP.assist + "어시스트 (" + String.format("%.2f", assistMVP.totalDamageDealt) + " DMG)", NamedTextColor.YELLOW),
                        Component.text("를 한 ", NamedTextColor.WHITE),
                        Component.text(assistMVP.getUser().getName(), NamedTextColor.AQUA),
                        Component.text("님 입니다.", NamedTextColor.WHITE)
                ).build());
            }

        } else {
            broadcast(Component.empty());
            broadcast(Component.text("  * ", NamedTextColor.RED).append(Component.text("게임 인원 수가 6명 미만이므로 MVP는 선정되지 않습니다.", NamedTextColor.GRAY)));
        }
        broadcast(Component.text("└─────────────────────────┘", NamedTextColor.GRAY));

        if (killMVP != null) {
            killMVP.getUser().getPlayer().ifPresent(player -> {
                AdvancementManager manager = Noah.getInstance().getAdvancementManager();
                AdvancementConstant[] constants = {
                        manager.getConstant(AdvancementKeys.MVP_KILLS_5),
                        manager.getConstant(AdvancementKeys.MVP_KILLS_15),
                        manager.getConstant(AdvancementKeys.MVP_KILLS_30),
                        manager.getConstant(AdvancementKeys.MVP_KILLS_50)
                };
                for (AdvancementConstant constant : constants) {
                    AdvancementProgress progress = player.getAdvancementProgress(constant.getAdvancement());
                    if (!progress.isDone()) {
                        progress.increaseCount();
                    }
                }
            });
        }
        if (assistMVP != null) {
            assistMVP.getUser().getPlayer().ifPresent(player -> {
                AdvancementManager manager = Noah.getInstance().getAdvancementManager();
                AdvancementConstant[] constants = {
                        manager.getConstant(AdvancementKeys.MVP_ASSIST_5),
                        manager.getConstant(AdvancementKeys.MVP_ASSIST_15),
                        manager.getConstant(AdvancementKeys.MVP_ASSIST_30),
                        manager.getConstant(AdvancementKeys.MVP_ASSIST_50)
                };
                for (AdvancementConstant constant : constants) {
                    AdvancementProgress progress = player.getAdvancementProgress(constant.getAdvancement());
                    if (!progress.isDone()) {
                        progress.increaseCount();
                    }
                }
            });
        }

        if (win != null) {
            AdvancementManager manager = Noah.getInstance().getAdvancementManager();
            AdvancementConstant[] constants = {
                    manager.getConstant(AdvancementKeys.WIN_20),
                    manager.getConstant(AdvancementKeys.WIN_50),
                    manager.getConstant(AdvancementKeys.WIN_100),
                    manager.getConstant(AdvancementKeys.WIN_250)
            };
            win.getParticipants().forEach(tdmParticipant -> {
                if (tdmParticipant.kill >= 5) {
                    tdmParticipant.getUser().getPlayer().ifPresent(player -> {
                        for (AdvancementConstant constant : constants) {
                            AdvancementProgress progress = player.getAdvancementProgress(constant.getAdvancement());
                            if (!progress.isDone()) {
                                progress.increaseCount();
                            }
                        }
                    });
                }
            });
        }

        int totalDamage = 0;
        Map<TDMTeam.Type, Integer> mmr = new HashMap<>();

        for (TDMTeam team : teams.values()) {
            mmr.put(team.getType(), 0);
            for (TDMParticipant participant : team.getParticipants()) {

                totalDamage += participant.totalDamageDealt;
                mmr.compute(team.getType(), (k, v) -> v + participant.getUser().getDataContainer().get(TDMEloRating.class).rating);

                participant.getUser().getPlayer().ifPresentOrElse(player -> {
                    player.hideBossBar(bossBar);
                    player.closeInventory(InventoryCloseEvent.Reason.PLUGIN); // 플레이어 인벤토리 닫기 (총기 선택창일 경우를 방지)
                    player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType())); // 플레이어 포션 효과 전부 제거
                    player.getInventory().clear(); // 플레이어 인벤토리 클리어

                    /* 플레이어 체력 원상복구 */
                    player.resetMaxHealth();
                    player.setHealth(player.getMaxHealth());

                    /* 플레이어 탭 리스트 이름 원상복구 */
                    PlayerListNameUtil.setDefault(player);

                    ChannelItemUtil.giveItem(player);
                    FMJ.getFMJPlayer(player).unsafeStatusReload(); // DEPRECATED
                }, () -> participant.getUser().setUnload(true)); /* 플레이어가 오프라인 일 시 언로드 리스트에 추가 */
                // 경험치 지급 공식 (kill x 2 + assist) x kill / kills
                int exp = red.kills + black.kills == 0 ? 0 : 10 * (participant.kill + participant.assist) * participant.kill / (red.kills + black.kills);
                LevelManager.addExp(participant.getUser(), exp);
            }
        }

        /* rating calculation */
        if (red.getParticipants().size() + black.getParticipants().size() >= 6) {
            for (TDMTeam.Type teamType : TDMTeam.Type.values()) {
                Collection<TDMParticipant> sides = getTeam(teamType.side()).getParticipants();
                int opAvgMMR = 0;
                for (TDMParticipant sideParticipant : sides) {
                    opAvgMMR += sideParticipant.getUser().getDataContainer().get(TDMEloRating.class).rating;
                }
                opAvgMMR /= sides.size();

                float score;
                if (win == null) score = 0.5f;
                else if (win.getType() == teamType) score = 1;
                else score = 0;

                for (TDMParticipant participant : getTeam(teamType).getParticipants()) {
                    User me = participant.getUser();
                    if (me.getPlayer().isPresent()) {
                        int k = Math.min(TDMEloRating.MAX_K, (int) (participant.totalDamageDealt / totalDamage * 100));
                        Pair<Integer, Integer> result = TDMEloUtil.calculate(me, opAvgMMR, score, k);
                        int before = result.key();
                        int after = me.getDataContainer().get(TDMEloRating.class).rating;
                        Component diff = result.value() > 0 ?
                                Component.text(" (+" + result.value() + ")", NamedTextColor.GREEN) :
                                Component.text(" (" + result.value() + ")", NamedTextColor.RED);
                        if (result.value() != 0) {
                            me.sendMessage(
                                    Component.text().append(
                                            Prefix.INFO_WITH_SPACE,
                                            Component.text("MMR 변동 됨. ", NamedTextColor.YELLOW),
                                            Component.text(before, NamedTextColor.GRAY),
                                            Component.text(" -> ", NamedTextColor.DARK_GRAY),
                                            Component.text(after, NamedTextColor.WHITE),
                                            diff
                                    ).build()
                            );
                        }
                    }
                }
            }
        }

        if (!map.unloadWorld()) { // 맵 언로드 시도
            LOGGER.warn("맵 언로딩 실패. map={}", map);
        }

        Bukkit.getPluginManager().callEvent(new TDMGameOverEvent(this));
    }

    private void calculateKDA() {
        teams.values().forEach(team -> team.getParticipants().forEach(p -> {
            p.getUser().getDataContainer().getOrLoadAsync(KDA.class).thenApply(kda -> {
                kda.kills += p.kill;
                kda.deaths += p.death;
                kda.assists += p.assist;
                return kda;
            });
        }));
    }

    private void calculateTier() {
        teams.values().forEach(team -> team.getParticipants().forEach(p -> {
            int amount = p.kill * 10 - p.death * 11;
            TierManager.addScore(p.getUser().getUniqueId(), amount);
        }));
    }

    private void updateBossBar() {
        bossBar.name(getBossBarTitle());
        int red = getTeam(TDMTeam.Type.RED).kills;
        int black = getTeam(TDMTeam.Type.BLACK).kills;
        int total = red + black;
        double progress = 0.5;
        if (red > black) progress += (double) red / total / 2;
        else if (red < black) progress -= (double) black / total / 2;
        bossBar.progress((float) (0.5 * Math.pow(0.5 * (2 * progress - 1), 3) + 0.5));
    }

    private Component getBossBarTitle() {
        Component time = Component.text(TimeUtil.formatTime(this.time));
        if (this.time <= TDMGameOption.GAME_BAR_POINT_TIME) time.color(NamedTextColor.RED);

        return Component.text().append(
                Component.text("RED - ", TDMGameOption.RED_TEAM_COLOR),
                Component.text(getTeam(TDMTeam.Type.RED).kills + " | ", NamedTextColor.GRAY),
                time,
                Component.text(" | " + this.getTeam(TDMTeam.Type.BLACK).kills, NamedTextColor.GRAY),
                Component.text(" - BLACK", TDMGameOption.BLACK_TEAM_COLOR)
        ).build();
    }

    public TDMTeam.Type getTeamType(Player player) {
        return getTeamType(player.getUniqueId());
    }

    public TDMTeam.Type getTeamType(UUID uniqueId) {
        return teamTypes.get(uniqueId);
    }

    public TDMTeam getTeam(TDMTeam.Type type) {
        return teams.get(type);
    }

    @Override
    public int getTime() {
        return time;
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (isInGame(event.getPlayer()) && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onGunDamage(WeaponDamageEntityEvent event) {
        if (!event.getVictim().getWorld().getName().equals(map.getWorldName())) return;
        if (event.getVictim() instanceof Player victim) {
            Player attacker = event.getPlayer();
            onDamageEvent(attacker, victim, event.getDamage(), event, event.getWeapon());
        }
    }

    @EventHandler
    public void onShoot(WeaponPrepareShootEvent event) {
        Player player = event.getPlayer();
        if (!isInGame(player)) {
            return;
        }
        if (getTeam(getTeamType(player)).getParticipant(player.getUniqueId()).isRespawning()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFKey(PlayerSwapHandItemsEvent event) {
        TDMTeam.Type type = getTeamType(event.getPlayer());
        if (type != null) {
            event.setCancelled(true);
            TDMParticipant participant = getTeam(type).getParticipant(event.getPlayer().getUniqueId());
            if (participant.isRespawning()) {
                TDMGunSelectInventory.open(participant);
            }
        }
    }

    @EventHandler
    public void onJump(PlayerJumpEvent event) {
        TDMTeam.Type type = getTeamType(event.getPlayer());
        if (type != null) {
            TDMParticipant participant = getTeam(type).getParticipant(event.getPlayer().getUniqueId());
            if (participant.isRespawning()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        ItemStack i = event.getPlayer().getInventory().getItemInMainHand();
        if (!event.getAction().isRightClick()) return;
        if (i.getType() == Material.AIR) return;
        TDMTeam.Type type = getTeamType(event.getPlayer());
        if (type != null) {
            event.setCancelled(true);
            ItemStack healKit = TDMGameOption.getHealKit();
            if (i.isSimilar(healKit)) {
                event.getPlayer().getInventory().remove(healKit);
                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 4));
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        TDMTeam.Type teamType = getTeamType(event.getPlayer());
        if (teamType != null) { // null 이 아니면 게임에 참여중인 유저
            TDMTeam team = getTeam(teamType);
            TDMParticipant participant = team.getParticipant(event.getPlayer().getUniqueId());
            initViewAndTeleport(participant);
            /* TODO:
            *   나간지 일정시간이 지나면 패널티를 부여 할 것인가?
            * */
        }
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getOrigin().isFromFalling()) event.setCancelled(true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        TDMTeam.Type teamType = getTeamType(player);
        if (teamType != null) {
            Player attacker = player.getKiller();
            if (attacker == null) return;
            deathPlayer(attacker, player, null, true);
        }
        /* TODO:
        *   패널티를 부여 할 것이면 퇴장 데이터 관리 클래스를 하나 만들어 처리하자
        *    */
    }

    /* TODO: AFK 방지 기능을 넣을 것 인가? 아마 자동화가 이루어질 것이므로 넣을 것 같다 */
    
    @EventHandler
    public void onWeaponBreakGlass(WeaponBreakGlassEvent e) {
        this.checkInGame(e.getPlayer(), e);
    }

    @EventHandler
    public void onWeaponBurnBlock(WeaponBurnBlockEvent e) {
        this.checkInGame(e.getShooter(), e);
    }

    /**
     * 플레이어가 게임에 참여 중인지 확인 후, 아니라면 이벤트를 캔슬합니다.
     *
     * @param player 플레이어
     * @param e 캔슬이 가능한 이벤트
     */
    void checkInGame(@NotNull final Player player, @NotNull final Cancellable e) {
        if (getTeamType(player) != null) e.setCancelled(true);
    }

    public static void giveWeapon(Player victim, TDMParticipant victimParticipant) {
        victim.getInventory().clear();
        FMJPlayer victimFMJ = FMJ.getFMJPlayer(victim);
        victimFMJ.resetWeaponData();
        Weapon main = victimParticipant.nextWeapon;
        if (victimFMJ.getWeapon() != null) {
            victimFMJ.setAmmo(main.getReload().getAmount());
        }
        if (main != null) {
            victim.getInventory().setItem(0, main.getItemStack());
        }
        Weapon sub = FMJ.findWeapon(TDMGameOption.SUB_WEAPON_KEY);
        if (sub != null) {
            victim.getInventory().setItem(1, sub.getItemStack());
        }
        victim.getInventory().setItem(3, TDMGameOption.getHealKit());
        victimFMJ.unsafeStatusReload(); // DEPRECATED
    }

    private void onDamageEvent(Player attacker, Player victim, double damage, Cancellable event, Weapon weapon) {
        TDMTeam.Type attackerTeam = getTeamType(attacker);
        TDMTeam.Type victimTeam = getTeamType(victim);

        if (attackerTeam != null && victimTeam != null) { // 두명 다 게임에 참여중일때를 체크
            if (attackerTeam == victimTeam) { // 팀킬 방지
                event.setCancelled(true);
                return;
            }

            TDMParticipant attackerParticipant = getTeam(attackerTeam).getParticipant(attacker.getUniqueId());
            TDMParticipant victimParticipant = getTeam(victimTeam).getParticipant(victim.getUniqueId());

            if (victimParticipant.godMode || victimParticipant.nextWeapon == null) {
                event.setCancelled(true);
                attacker.sendActionBar(Component.text(victim.getName() + "님은 현재 무적상태 입니다.", NamedTextColor.GRAY));
                return;
            }

            attackerParticipant.addDamageDealt(victim.getUniqueId(), damage);
            attackerParticipant.totalDamageDealt += damage;
            getTeam(attackerTeam).totalDamageDealt += damage;

            victim.setKiller(attacker);  // onQuit 시 attacker 처리를 위한 코드

            if (victim.getHealth() < damage) { // onDeath
                event.setCancelled(true);
                onDeathEvent(attacker, victim, weapon);
            }
        }
    }

    private void deathPlayer(Player attacker, Player victim, Weapon weapon, boolean onQuit) {
        TDMTeam.Type attackerTeam = getTeamType(attacker);
        TDMTeam.Type victimTeam = getTeamType(victim);

        TDMParticipant attackerParticipant = getTeam(attackerTeam).getParticipant(attacker.getUniqueId());
        TDMParticipant victimParticipant = getTeam(victimTeam).getParticipant(victim.getUniqueId());

        victimParticipant.killStreak = 0;

        victim.setHealth(victim.getMaxHealth());
        Location spawnPoint = map.getSpawnPoint(victimTeam);
        if (!onQuit) victim.teleport(spawnPoint); // 텔레포트에 실패할 시 로그를 찍는가?

        attackerParticipant.kill++;
        getTeam(attackerTeam).kills++;

        victimParticipant.death++;

        /* assist calculation */
        Set<TDMParticipant> assistors = new HashSet<>();
        for (TDMParticipant participant : getTeam(attackerTeam).getParticipants()) {
            if (!participant.equals(attackerParticipant)) { // 킬을 낸 사람은 어시스트에서 제외
                if (participant.getDamageDealt(victim.getUniqueId()) >= TDMGameOption.ASSIST_DAMAGE_THRESHOLD) {
                    participant.assist++;
                    getTeam(attackerTeam).assists++;
                    assistors.add(participant);
                }
                participant.getDamageDealt().remove(victim.getUniqueId());
            }
        }

        Bukkit.getPluginManager().callEvent(new ParticipantKillEnemyEvent(attackerParticipant, victimParticipant, assistors, this, weapon));

        updateBossBar();
    }

    private boolean isInGame(Player player) {
        return !Lobby.getRegion().isInside(player) && getTeamType(player) != null;
    }

    private void onDeathEvent(Player attacker, Player victim, Weapon weapon) {
        deathPlayer(attacker, victim, weapon, false);

        TDMTeam.Type attackerTeam = getTeamType(attacker);
        TDMTeam.Type victimTeam = getTeamType(victim);

        TDMParticipant attackerParticipant = getTeam(attackerTeam).getParticipant(attacker.getUniqueId());
        TDMParticipant victimParticipant = getTeam(victimTeam).getParticipant(victim.getUniqueId());


        Component barDisplay = Component.empty();

        /* display kill streak */
        KillStreak killStreak = KillStreak.fromKills(++attackerParticipant.killStreak);
        if (killStreak != null) {
            Title title = new TitleBuilder()
                    .setSubTitle(Component.text(killStreak.getDisplay()))
                    .setIn(500)
                    .setStay(500)
                    .setOut(2000)
                    .build();
            attacker.showTitle(title);
            barDisplay = Component.text(killStreak.getBarDisplay());


            /* Kill Streak Advancement */
            AdvancementManager manager = Noah.getInstance().getAdvancementManager();
            AdvancementConstant constant = null;
            if (killStreak.getKills() == 2) {
                constant = manager.getConstant(AdvancementKeys.KILL_STREAK2_20);
            }
            if (killStreak.getKills() == 3) {
                constant = manager.getConstant(AdvancementKeys.KILL_STREAK3_30);
            }
            if (killStreak.getKills() == 4) {
                constant = manager.getConstant(AdvancementKeys.KILL_STREAK4_30);
            }
            if (killStreak.getKills() == 5){
                constant = manager.getConstant(AdvancementKeys.KILL_STREAK5_30);
            }
            if (constant != null) {
                AdvancementProgress progress = attacker.getAdvancementProgress(constant.getAdvancement());
                if (!progress.isDone()) {
                    progress.increaseCount();
                }
            }
        }

        Component weaponName = weapon != null ? weapon.getItemStack().displayName() : Component.text("손");

        /* kill logging */
        Component killLog = Component.text().append(
                Component.text(attacker.getName() + " ", attackerTeam.getColor()),
                barDisplay,
                Component.text(" killed ", NamedTextColor.GRAY),
                Component.text(victim.getName(), victimTeam.getColor()),
                Component.text(" with ", NamedTextColor.GRAY),
                weaponName

        ).build();
        for (UUID uuid : teamTypes.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && GameManager.getInstance().isInGame(player)) {
                KillLogger.display(player, killLog);
            }
        }

        updateTabListName(attackerParticipant);
        updateTabListName(victimParticipant);

        attacker.playSound(attacker.getLocation(), "tdm.kill", 1, 1);

        /* add potion effects */
        victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 1));
        victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 9));

        /* heal victim */
        victim.setHealth(victim.getMaxHealth());

        victimParticipant.godMode = true;
        victimParticipant.respawnTime = TDMGameOption.RESPAWN_TIME;
        BukkitTask timer = Bukkit.getScheduler().runTaskTimer(Noah.getInstance(), () -> {
            if (!isInGame(victim)) return; // 게임에 참여중인지 확인
            Title title = new TitleBuilder()
                    .setTitle(
                            Component.text().append(
                                    Component.text("리스폰 까지 ", NamedTextColor.RED),
                                    Component.text((victimParticipant.respawnTime--) + "초", NamedTextColor.GRAY)
                            ).build())
                    .setSubTitle(Component.text("F키 로 총기를 변경하실 수 있습니다", NamedTextColor.YELLOW))
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

            if (!isInGame(victim)) return; // 게임에 참여중인지 확인

            /* remove potion effects */
            victim.removePotionEffect(PotionEffectType.BLINDNESS);
            victim.removePotionEffect(PotionEffectType.SLOW);
            victim.closeInventory(InventoryCloseEvent.Reason.PLUGIN);

            /* give a weapon to victim */
            try {
                giveWeapon(victim, victimParticipant);
            } finally {
                /* remove god mode from victim */
                BukkitTask godTask = Bukkit.getScheduler().runTaskLater(Noah.getInstance(), () -> {
                    victimParticipant.godMode = false;
                    if (!isInGame(victim)) return; // 게임에 참여중인지 확인
                    Title title = new TitleBuilder()
                            .setSubTitle(Component.text("무적 상태가 해제 되었습니다.", NamedTextColor.GRAY))
                            .setIn(0)
                            .setStay(1000)
                            .setOut(500)
                            .build();
                    victim.showTitle(title);
                }, 20L * TDMGameOption.GOD_MODE_DURATION);
                tasks.add(godTask);
                Title title = new TitleBuilder()
                        .setTitle(Component.text("리스폰 완료!", NamedTextColor.GREEN))
                        .setSubTitle(Component.text(String.format("%d초의 무적시간이 적용됩니다.", TDMGameOption.GOD_MODE_DURATION),
                                NamedTextColor.GRAY)
                        )
                        .setIn(0)
                        .setStay(1000)
                        .setOut(500)
                        .build();
                victim.sendActionBar(Component.empty());
                victim.showTitle(title);
            }
        }, 20 * TDMGameOption.RESPAWN_TIME);
        tasks.add(timer);
        tasks.add(later);
        tasks.removeIf(BukkitTask::isCancelled);
    }
}
