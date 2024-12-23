package com.ar.askgaming.happyhour.ModesFromIntegrations;

import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.ar.askgaming.happyhour.HHPlugin;
import com.ar.askgaming.happyhour.HappyHour;
import com.ar.askgaming.happyhour.Managers.HHManager.Mode;
import com.gamingmesh.jobs.api.JobsExpGainEvent;
import com.gamingmesh.jobs.api.JobsPaymentEvent;
import com.gamingmesh.jobs.container.CurrencyType;

public class Jobs implements Listener{

    private HHPlugin plugin;
    public Jobs(HHPlugin plugin){
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler
    public void onExpGain(JobsExpGainEvent e){

        List<HappyHour> activeHappyHours = plugin.getManager().getActiveHappyHours();
        if (activeHappyHours.isEmpty()) {
            return;
        }
        for (HappyHour hh : activeHappyHours) {
            if (hh.getActualMode() == Mode.JOBS || hh.getActualMode() == Mode.ALL) {
                double chance = plugin.getConfig().getDouble("modes.jobs.chance");
                double multiplier = plugin.getConfig().getDouble("modes.jobs.multiplier");

                if (Math.random() < chance) {
                    e.setExp(e.getExp() * multiplier);
                    //Bukkit.broadcastMessage("Debug: " + e.getExp());
                }
            }
        }
    }

    @EventHandler
    public void onPayment(JobsPaymentEvent e){

        List<HappyHour> activeHappyHours = plugin.getManager().getActiveHappyHours();
        if (activeHappyHours.isEmpty()) {
            return;
        }
        for (HappyHour hh : activeHappyHours) {
            if (hh.getActualMode() == Mode.JOBS || hh.getActualMode() == Mode.ALL) {
                double chance = plugin.getConfig().getDouble("modes.jobs.chance");
                double multiplier = plugin.getConfig().getDouble("modes.jobs.multiplier");

                if (Math.random() < chance) {
                    e.set(CurrencyType.MONEY, e.get(CurrencyType.MONEY) * multiplier);
                    //Bukkit.broadcastMessage("Debug: " + e.get(CurrencyType.MONEY));
                }
            }
        }
    }   
}
