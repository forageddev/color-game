package dev.foraged.colorgame.player

import dev.foraged.game.player.GamePlayer
import org.jetbrains.annotations.NotNull

class ColorGamePlayer extends GamePlayer implements Comparable<ColorGamePlayer> {

    static ColorGamePlayerColor nextColor = ColorGamePlayerColor.RED

    ColorGamePlayerColor color

    ColorGamePlayer(UUID id) {
        super(id)
        color = nextColor
        nextColor = nextColor.next
    }

    @Override
    int compareTo(@NotNull ColorGamePlayer o) {
        return o.coins - coins
    }
}
