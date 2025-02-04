package com.ar.askgaming.happyhour.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.ar.askgaming.happyhour.HHPlugin;

public class PlayerJoinListener implements Listener{

    private HHPlugin plugin;
    public PlayerJoinListener(HHPlugin plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        if (plugin.getManager().getActiveHappyHours().isEmpty()) {
            return;

        }
        plugin.getScoreBoard().addPlayer(e.getPlayer());
    }

}
