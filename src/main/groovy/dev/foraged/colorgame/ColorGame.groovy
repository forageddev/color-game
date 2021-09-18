package dev.foraged.colorgame

import dev.foraged.game.Game
import dev.foraged.game.arena.impl.UnlimitedArena
import dev.foraged.game.board.GameBoardAdapter
import dev.foraged.game.task.GameTask
import dev.foraged.game.util.CC
import dev.foraged.game.util.TimeUtil
import dev.foraged.colorgame.player.ColorGamePlayer
import dev.foraged.colorgame.player.ColorGamePlayerState

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

import java.text.SimpleDateFormat

class ColorGame extends Game<ColorGamePlayer, UnlimitedArena> implements GameBoardAdapter {

    ColorGameState gameState
    var _startTime = 30

    ColorGame(ColorGamePlugin plugin) {
        super(plugin, "Color Run", "Avoid the enemy blocks and try to\ncontrol the most blocks within\n2 minutes!")
        arena = new UnlimitedArena()
        arena.spawnPoints = new Location(Bukkit.getWorlds()[0], 100, 5, 100)
        gameState = ColorGameState.WAITING
    }

    @Override
    void ready() {
        gameState = ColorGameState.STARTING
        new BukkitRunnable() {
            @Override
            void run() {
                if (gameState == ColorGameState.ACTIVE) {
                    cancel()
                    return
                }

                if (players.size() < 2 && gameState == ColorGameState.STARTING) {
                    _startTime = 60
                    gameState = ColorGameState.WAITING
                    cancel()
                    return
                }

                _startTime--
                if (_startTime == 0) {
                    start()
                    cancel()
                } else if (_startTime % 5 == 0 || _startTime <= 5) {
                    var color
                    if (_startTime > 10) color = "&e"
                    else if (_startTime > 5) color = "&6"
                    else color = "&c"
                    broadcast("&eThe game starts in ${color}${_startTime}&e second${_startTime == 1 ? "" : "s"}!")
                    players().each {it.playSound(it.location, Sound.BLOCK_COMPARATOR_CLICK, 1f, 1f)}
                }
            }
        }.runTaskTimer(plugin, 20L, 20L)
    }

    @Override
    void start() {
        super.start()
        broadcast("&a&lThe game has started!")
        gameState = ColorGameState.ACTIVE
        players().each {it.allowFlight = true}
        new GameTask(plugin, () -> stop()).delay(2400L).complete()
    }

    @Override
    void stop() {
        super.stop()
        gameState = ColorGameState.ENDING
        new GameTask(plugin, () -> {
            players().each {
                ColorGamePlayer data = getPlayerData(it)
                buildWrapper("Reward Summary", "  &7You earned\n      &f▪ &b${data.coins} Party Games Coins\n      &f▪ &30 Twoot Experience\n&f").toList().forEach(s -> it.sendMessage(CC.translate(s)))
            }
        }).delay(60L).then(() -> Bukkit.shutdown()).delay(240L).complete()
    }

    @Override
    void join(Player player) {
        super.join(player)
        players.put(player.uniqueId, new ColorGamePlayer(player.uniqueId))

        if (players.size() == 2) ready()
    }

    @Override
    String getTitle(Player player) {
        return "&e&lCOLOR RUN"
    }

    @Override
    List<String> getLines(Player player) {
        ColorGamePlayer data = getPlayerData(player)
        var required = 2 - players.size()

        switch (gameState) {
            case ColorGameState.WAITING: {
                return [
                        "&7${new SimpleDateFormat("dd/MM/yy").format(new Date(System.currentTimeMillis()))}",
                        "",
                        "&fMap: &a${arena.class.simpleName}",
                        "&fPlayers: &a${players.size()}/${Bukkit.maxPlayers}",
                        "",
                        "&fWaiting for &a${required}&f more",
                        "&fplayer${required == 1 ? "" : "s"} to join",
                        "",
                        "&fGame: &a${name}",
                        "",
                        "&ewww.twoot.tk"
                ]
            }
            case ColorGameState.STARTING: {
                return [
                        "&7${new SimpleDateFormat("dd/MM/yy").format(new Date(System.currentTimeMillis()))}",
                        "",
                        "&fMap: &a${arena.class.simpleName}",
                        "&fPlayers: &a${players.size()}/${Bukkit.maxPlayers}",
                        "",
                        "&fStarting in &a${new SimpleDateFormat("mm:ss").format(new Date(_startTime * 1000))}&f to",
                        "&fallow time for",
                        "&fadditional players",
                        "",
                        "&fGame: &a${name}",
                        "",
                        "&ewww.twoot.tk"
                ]
            }
            case ColorGameState.ACTIVE:
            case ColorGameState.ENDING: {
                return [
                        "&7Duration: ${TimeUtil.millisToRoundedTime(System.currentTimeMillis() - started)}",
                        "",
                        "&fColor: &a${data.color.displayName}",
                        "",
                        "&fPlayers Alive: &a${players.values().stream().filter(it -> it.state == ColorGamePlayerState.ALIVE).count()}",
                        "",
                        "&fBlocks Captured: &a${data.coins as int}",
                        "",
                        "&7${new SimpleDateFormat("dd/MM/yy").format(new Date(System.currentTimeMillis()))}",
                        "&ewww.twoot.tk"
                ]
            }
        }
    }
}
