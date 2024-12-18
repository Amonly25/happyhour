package com.ar.askgaming.happyhour.CustomEvent;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.ar.askgaming.happyhour.HHPlugin;

public class HappyHourStartListener implements Listener{

    private HHPlugin plugin;
    public HappyHourStartListener(HHPlugin plugin){
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler
    public void onHappyHourStart(HappyHourStartEvent event){
        //TODO
        Bukkit.broadcastMessage("Event HH: " + event.getMode().name());
    }

}
