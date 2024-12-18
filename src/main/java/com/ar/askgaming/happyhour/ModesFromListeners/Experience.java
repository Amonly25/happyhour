package com.ar.askgaming.happyhour.ModesFromListeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import com.ar.askgaming.happyhour.HHManager.Mode;
import com.ar.askgaming.happyhour.HHPlugin;
import com.ar.askgaming.happyhour.HappyHour;

public class Experience implements Listener{

    private HHPlugin plugin;
    public Experience(HHPlugin plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler
    public void onExpGain(EntityDeathEvent e){
        if (!(e.getEntity().getKiller() instanceof Player)) {
            return;
        }
        List<HappyHour> activeHappyHours = plugin.getManager().getActiveHappyHours();
        if (activeHappyHours.isEmpty()) {
            return;
        }
        Entity type = e.getEntity();
        for (HappyHour hh : activeHappyHours) {
            if (hh.getActualMode() == Mode.EXPERIENCE) {
                if (!(type instanceof Player)) {
                    int multiplier = plugin.getConfig().getInt("happyhour.experience.multiplier");
                    double chance = plugin.getConfig().getDouble("happyhour.experience.chance");
                    if (Math.random() < chance) {
                        int exp = e.getDroppedExp();
                        type.getWorld().spawn(type.getLocation(), ExperienceOrb.class).setExperience(exp * multiplier);
                        //DEBUG
                        Bukkit.broadcastMessage("Dropped " + exp * (multiplier-1) + " experience");
                    }
                }
            }
        }

    } 
}
