package com.nopluginsfound;

import org.bukkit.plugin.java.JavaPlugin;

public class NoPluginsFound extends JavaPlugin {

    @Override
    public void onEnable() {
        // Registers the security listener to start blocking hacked client requests
        getServer().getPluginManager().registerEvents(new NoPluginsFoundListener(), this);
        getLogger().info("NoPluginsFound is now guarding your server plugins!");
    }

    @Override
    public void onDisable() {
        getLogger().info("NoPluginsFound has been disabled.");
    }
}
