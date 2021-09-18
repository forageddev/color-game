package dev.foraged.colorgame

import org.bukkit.plugin.java.JavaPlugin

class ColorGamePlugin extends JavaPlugin {

    static ColorGamePlugin instance
    ColorGame game

    @Override
    void onEnable() {
        game = new ColorGame(instance = this)
    }
}
