package dev.foraged.colorgame


import dev.foraged.game.Game
import dev.foraged.game.arena.impl.UnlimitedArena
import dev.foraged.game.board.GameBoardAdapter
import dev.foraged.game.task.GameTask
import dev.foraged.game.task.impl.GameCountdownTask
import dev.foraged.game.util.CC
import dev.foraged.game.util.ItemBuilder
import dev.foraged.game.util.TimeUtil
import dev.foraged.colorgame.player.ColorGamePlayer

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player

import java.text.SimpleDateFormat

class ColorGame extends Game<ColorGamePlayer, UnlimitedArena, ColorGameState> implements GameBoardAdapter {

    ColorGame(ColorGamePlugin plugin) {
        super(plugin, "Color Run", "Avoid the enemy blocks and try to\ncontrol the most blocks within\n2 minutes!")
        arena = new UnlimitedArena()
        arena.spawnPoints = new Location(Bukkit.getWorlds()[0], 100, 5, 100)
        gameState = ColorGameState.WAITING
    }

    @Override
    void ready() {
        countdownTask = new GameCountdownTask(this, 30, 2)
        gameState = ColorGameState.STARTING
    }

    @Override
    void start() {
        super.start()
        broadcast("&b&lThe game has started!")
        gameState = ColorGameState.ACTIVE
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
        ColorGamePlayer data = new ColorGamePlayer(player.uniqueId);
        players.put(player.uniqueId, data)
        super.join(player)

        player.inventory.helmet = new ItemBuilder(Material.LEATHER_HELMET).color(data.color.dye.color).name(data.color.displayName).build()
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
                        arenaInfo,
                        "",
                        "&fMap: &b${arena.name}",
                        "&fPlayers: &b${players.size()}/${Bukkit.maxPlayers}",
                        "",
                        "&fWaiting for &b${required}&f more",
                        "&fplayer${required == 1 ? "" : "s"} to join",
                        "",
                        "&fGame: &b${name}",
                        "",
                        footer
                ]
            }
            case ColorGameState.STARTING: {
                return [
                        arenaInfo,
                        "",
                        "&fMap: &b${arena.name}",
                        "&fPlayers: &b${players.size()}/${Bukkit.maxPlayers}",
                        "",
                        "&fStarting in &b${new SimpleDateFormat("mm:ss").format(new Date(countdownTask.time * 1000))}&f to",
                        "&fallow time for",
                        "&fadditional players",
                        "",
                        "&fGame: &b${name}",
                        "",
                        footer
                ]
            }
            case ColorGameState.ACTIVE:
            case ColorGameState.ENDING: {
                return [
                        "&7Duration: ${TimeUtil.formatTime(System.currentTimeMillis() - started)}",
                        "",
                        "&fColor: &b${data.color.displayName}",
                        "",
                        "&fPlayers Alive: &b${players.values().stream().filter(it -> it.state == ColorGamePlayerState.ALIVE).count()}",
                        "",
                        "&fBlocks Captured: &b${data.coins as int}",
                        "",
                        arenaInfo,
                        footer
                ]
            }
        }
    }
}
