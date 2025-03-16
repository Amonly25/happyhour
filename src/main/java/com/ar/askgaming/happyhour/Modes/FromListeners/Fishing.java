package com.ar.askgaming.happyhour.Modes.FromListeners;

import java.util.List;

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

        plugin.getChallengeManager().increaseProgress(Mode.FISHING, e.getPlayer(), null, null);

        List<HappyHour> activeHappyHours = plugin.getManager().getActiveHappyHours();
        if (activeHappyHours.isEmpty()) {
            return;
        }
        for (HappyHour hh : activeHappyHours) {
            Item item = (Item) e.getCaught();
            Mode mode = hh.getActualMode();
            if (mode == Mode.FISHING || mode == Mode.ALL) {
                applyMultiplier(item, "modes.fishing.chance", "modes.fishing.multiplier", e.getPlayer().getLocation());
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
               // Bukkit.broadcastMessage("Dropped item: " + item.getItemStack().getType().name());
            }
        }
    }
}
