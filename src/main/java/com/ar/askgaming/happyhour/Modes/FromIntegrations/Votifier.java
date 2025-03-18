package com.ar.askgaming.happyhour.Modes.FromIntegrations;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.ar.askgaming.happyhour.HHPlugin;
import com.ar.askgaming.happyhour.HappyHour;
import com.ar.askgaming.happyhour.Challenges.ChallengeManager;
import com.ar.askgaming.happyhour.HHManager.Mode;
import com.vexsoftware.votifier.model.VotifierEvent;

public class Votifier implements Listener {

    private HHPlugin plugin;
    public Votifier(HHPlugin plugin){
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onVote(VotifierEvent e){
        String playerName = e.getVote().getUsername();

        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            return;
        }

        plugin.getChallengeManager().increaseProgress(ChallengeManager.Mode.VOTIFIER, player, null, null);

        List<HappyHour> activeHappyHours = plugin.getManager().getActiveHappyHours();
        if (activeHappyHours.isEmpty()) {
            return;
        }
        for (HappyHour hh : activeHappyHours) {
            if (hh.getActualMode() == Mode.VOTIFIER || hh.getActualMode() == Mode.ALL) {

                FileConfiguration config = plugin.getConfig();
                for (String key : config.getConfigurationSection("modes.votifier.rewards").getKeys(false)) {
                                        
                    String message = config.getString("modes.votifier.rewards." + key + ".message");
                    List<String> commands = config.getStringList("modes.votifier.rewards." + key + ".commands");
                    double chance = config.getDouble("modes.votifier.rewards." + key + ".chance");
                    
                    if (Math.random() < chance) {
                        if (!message.equals("")) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
                    
                        for (String s : commands){
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), s.replace("%player%", player.getName()));
                        }
                    }
                }
            }
        }
    }
}
