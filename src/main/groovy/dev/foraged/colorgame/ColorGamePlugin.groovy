package dev.foraged.colorgame

import dev.foraged.colorgame.listener.ColorGameListener
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class ColorGamePlugin extends JavaPlugin {

    static ColorGamePlugin instance
    ColorGame game

    @Override
    void onEnable() {
        instance = this
        game = new ColorGame(this)

        register(new ColorGameListener())
    }

    void register(Listener... listeners) {
        for (Listener listener : listeners) server.pluginManager.registerEvents(listener, this)
    }
}
