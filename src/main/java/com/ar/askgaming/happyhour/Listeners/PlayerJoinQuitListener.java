package com.ar.askgaming.happyhour.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.ar.askgaming.happyhour.HHPlugin;

public class PlayerJoinQuitListener implements Listener{

    private HHPlugin plugin;
    public PlayerJoinQuitListener(HHPlugin plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        plugin.getChallengeManager().loadPlayerChallenges(p);
        if (plugin.getManager().getActiveHappyHours().isEmpty()) {
            return;
        }
        plugin.getScoreBoard().addPlayer(p);
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();
        plugin.getChallengeManager().savePlayerChallenges(p);
        plugin.getChallengeManager().savePlayerData();
        plugin.getScoreBoard().removePlayer(p);
    }
}
