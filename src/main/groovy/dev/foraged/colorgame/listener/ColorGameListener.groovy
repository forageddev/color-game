package dev.foraged.colorgame.listener

import dev.foraged.game.task.GameTask
import dev.foraged.game.util.CC
import dev.foraged.colorgame.ColorGame
import dev.foraged.colorgame.ColorGamePlugin
import dev.foraged.colorgame.ColorGameState
import dev.foraged.colorgame.player.ColorGamePlayer
import dev.foraged.colorgame.player.ColorGamePlayerState
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class ColorGameListener implements Listener {

    ColorGame game = ColorGamePlugin.instance.game

    @EventHandler
    void onJoin(PlayerJoinEvent e) {
        Player player = e.player

        e.joinMessage = null

        if (game.gameState == ColorGameState.WAITING || game.gameState == ColorGameState.STARTING) {
            game.join(player)
        } else {
            e.player.kickPlayer(CC.translate("&cGame already started go awsay loasser")) //TODO: CHANGE THIS
        }
    }

    @EventHandler
    void onQuit(PlayerQuitEvent e) {
        Player player = e.player

        e.quitMessage = null
        game.leave(player)
    }

    @EventHandler
    void onDamage(EntityDamageEvent e) {
        e.cancelled = true
    }

    @EventHandler
    void onHungerChange(FoodLevelChangeEvent e) {
        e.cancelled = true
    }

    @EventHandler
    void onMove(PlayerMoveEvent e) {
        if (game.gameState != ColorGameState.ACTIVE) return

        Player player = e.player
        Location from = e.from, to = e.to
        if (from.blockX != to.blockX || from.blockY != to.blockY || from.blockZ != to.blockZ) {
            Block relative = to.block.getRelative(BlockFace.DOWN)
            ColorGamePlayer data = game.getPlayerData(player.uniqueId)

            if (data != null && data.state == ColorGamePlayerState.ALIVE) {
                if (relative.type == Material.BARRIER) {
                    new GameTask(game.plugin, () -> {
                            data.coins = data.coins + 1
                            relative.type = data.color.wool
                    }).delay(10L).complete()
                } else if (!relative.type.name().contains(data.color.wool.name())) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 1))
                    player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 200, 128))
                }
            }
        }
    }
}
