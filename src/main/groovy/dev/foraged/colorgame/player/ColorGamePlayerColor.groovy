package dev.foraged.colorgame.player

import org.bukkit.ChatColor
import org.bukkit.DyeColor
import org.bukkit.Material

enum ColorGamePlayerColor {

    RED(ChatColor.RED, DyeColor.RED),
    ORANGE(ChatColor.GOLD, DyeColor.ORANGE),
    YELLOW(ChatColor.YELLOW, DyeColor.YELLOW),
    LIME(ChatColor.GREEN, DyeColor.LIME),
    GREEN(ChatColor.DARK_GREEN, DyeColor.GREEN),
    AQUA(ChatColor.AQUA, DyeColor.LIGHT_BLUE),
    CYAN(ChatColor.DARK_AQUA, DyeColor.CYAN),
    BLUE(ChatColor.BLUE, DyeColor.BLUE)

    ChatColor color
    DyeColor dye

    ColorGamePlayerColor(ChatColor color, DyeColor dye) {
        this.color = color
        this.dye = dye
    }

    String getDisplayName() {
        return color.toString() + name().toLowerCase().capitalize()
    }

    Material getWool() {
        return Material.valueOf("${dye.name()}_WOOL")
    }

    Material getStainedClay() {
        return Material.valueOf("${dye.name()}_STAINED_CLAY")
    }

    Material getStainedGlass() {
        return Material.valueOf("${dye.name()}_STAINED_GLASS")
    }

    ColorGamePlayerColor getNext() {
        if (this == BLUE) return RED
        return values()[ordinal() + 1]
    }
}