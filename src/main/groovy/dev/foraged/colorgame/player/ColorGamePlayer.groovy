package dev.foraged.colorgame.player

import dev.foraged.game.player.GamePlayer
import org.jetbrains.annotations.NotNull

class ColorGamePlayer extends GamePlayer implements Comparable<ColorGamePlayer> {

    static ColorGamePlayerColor nextColor = ColorGamePlayerColor.RED

    ColorGamePlayerColor color
    ColorGamePlayerState state

    ColorGamePlayer(UUID id) {
        super(id)
        color = nextColor
        nextColor = ColorGamePlayerColor.values()[nextColor.ordinal() + 1]

        state = ColorGamePlayerState.ALIVE
    }

    @Override
    int compareTo(@NotNull ColorGamePlayer o) {
        return o.coins - coins
    }
}
