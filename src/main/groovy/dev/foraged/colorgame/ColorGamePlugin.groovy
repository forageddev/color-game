package dev.foraged.colorgame

import dev.foraged.colorgame.listener.ColorGameListener
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class ColorGamePlugin extends JavaPlugin {

    static ColorGamePlugin instance
    ColorGame game

    @Override
    void onEnable() {
        game = new ColorGame(instance = this)

        Bukkit.pluginManager.registerEvents(new ColorGameListener(), this)
    }
}
