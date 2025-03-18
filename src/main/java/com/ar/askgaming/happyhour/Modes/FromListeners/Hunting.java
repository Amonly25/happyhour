package com.ar.askgaming.happyhour.Modes.FromListeners;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Enemy;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.ar.askgaming.happyhour.HHPlugin;
import com.ar.askgaming.happyhour.HappyHour;
import com.ar.askgaming.happyhour.Challenges.ChallengeManager;
import com.ar.askgaming.happyhour.HHManager.Mode;

public class Hunting implements Listener{

    private HHPlugin plugin;
    public Hunting(HHPlugin plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler
    public void onKill(EntityDeathEvent e) {
        Entity entity = e.getEntity();
        
        if (!(e.getEntity().getKiller() instanceof Player)) {
            return;
        }
    
        Player player = e.getEntity().getKiller();
        
        if (entity instanceof Animals) {
            plugin.getChallengeManager().increaseProgress(ChallengeManager.Mode.HUNTING_ANIMALS, player, entity.getType(), null);
        } else if (entity instanceof Enemy) {
            plugin.getChallengeManager().increaseProgress(ChallengeManager.Mode.HUNTING_ENEMYS, player, entity.getType(), null);
        }
    
        List<HappyHour> activeHappyHours = plugin.getManager().getActiveHappyHours();
        if (activeHappyHours.isEmpty()) {
            return;
        }
    
        List<ItemStack> drops = e.getDrops();
        for (HappyHour hh : activeHappyHours) {
            Mode mode = hh.getActualMode();
            
            if (entity instanceof Animals && (mode == Mode.HUNTING_ANIMALS || mode == Mode.ALL)) {
                applyMultiplier(drops, "modes.hunting_animals.chance", "modes.hunting_animals.multiplier", entity.getLocation());
                break; 
            }
            if (entity instanceof Enemy && (mode == Mode.HUNTING_ENEMYS || mode == Mode.ALL)) {
                applyMultiplier(drops, "modes.hunting_enemys.chance", "modes.hunting_enemys.multiplier", entity.getLocation());
                break; 
            }
        }
    }
    
    private void applyMultiplier(List<ItemStack> drops, String chanceKey, String multiplierKey, Location loc) {
        double chance = plugin.getConfig().getDouble(chanceKey);
        double multiplier = plugin.getConfig().getDouble(multiplierKey);
        List<String> blackList = plugin.getConfig().getStringList("modes.hunting_enemys.blacklist_items");
        if (Math.random() < chance) {
            for (ItemStack drop : drops) {
                if (blackList.contains(drop.getType().name())) {
                    continue;
                }
                for (int i = 1; i < multiplier; i++)
                loc.getWorld().dropItem(loc, drop);
                //DEBUG
                //Bukkit.broadcastMessage("Dropped item: " + drop.getType().name());
            }
        }
    }
}
