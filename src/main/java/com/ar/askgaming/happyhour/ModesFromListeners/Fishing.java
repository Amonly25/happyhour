package com.ar.askgaming.happyhour.ModesFromListeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;

import com.ar.askgaming.happyhour.HHManager.Mode;
import com.ar.askgaming.happyhour.HHPlugin;
import com.ar.askgaming.happyhour.HappyHour;

public class Fishing implements Listener{

    private HHPlugin plugin;
    public Fishing(HHPlugin plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler
    public void onFish(PlayerFishEvent e){
        if (!(e.getState() == State.CAUGHT_FISH)) {
            return;
        }

        List<HappyHour> activeHappyHours = plugin.getManager().getActiveHappyHours();
        if (activeHappyHours.isEmpty()) {
            return;
        }
        for (HappyHour hh : activeHappyHours) {
            Item item = (Item) e.getCaught();
            if (hh.getActualMode() == Mode.FISHING) {
                applyMultiplier(item, "happyhour.fishing.chance", "happyhour.fishing.multiplier", e.getPlayer().getLocation());
            }
        }
    }
    private void applyMultiplier(Item item, String chanceKey, String multiplierKey, Location loc) {
        double chance = plugin.getConfig().getDouble(chanceKey);
        double multiplier = plugin.getConfig().getDouble(multiplierKey);
        if (Math.random() < chance) {
            for (int i = 1; i < multiplier; i++) {
                loc.getWorld().dropItem(loc, item.getItemStack());
                //DEBUG
                Bukkit.broadcastMessage("Dropped item: " + item.getItemStack().getType().name());
            }
        }
    }
}
