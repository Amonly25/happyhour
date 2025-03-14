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
import com.ar.askgaming.happyhour.HHManager.Mode;

public class Hunting implements Listener{

    private HHPlugin plugin;
    public Hunting(HHPlugin plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler
    public void onKill(EntityDeathEvent e){
        if (!(e.getEntity().getKiller() instanceof Player)) {
            return;
        }
        List<HappyHour> activeHappyHours = plugin.getManager().getActiveHappyHours();
        if (activeHappyHours.isEmpty()) {
            return;
        }
        for (HappyHour hh : activeHappyHours) {
            List<ItemStack> drops = e.getDrops();
            Entity type = e.getEntity();
            if ((hh.getActualMode() == Mode.HUNTING_ANIMALS || hh.getActualMode() == Mode.ALL) && type instanceof Animals) {
                applyMultiplier(drops, "modes.hunting_animals.chance", "modes.hunting_animals.multiplier", type.getLocation());
                plugin.getChallengeManager().increaseProgress(Mode.HUNTING_ANIMALS);
            } else if ((hh.getActualMode() == Mode.HUNTING_ENEMYS || hh.getActualMode() == Mode.ALL) && type instanceof Enemy) {
                applyMultiplier(drops, "modes.hunting_enemys.chance", "modes.hunting_enemys.multiplier", type.getLocation());
                plugin.getChallengeManager().increaseProgress(Mode.HUNTING_ENEMYS);
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
