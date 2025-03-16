package com.ar.askgaming.happyhour.Misc;

import org.bukkit.OfflinePlayer;

import com.ar.askgaming.happyhour.HHPlugin;
import com.ar.askgaming.happyhour.HappyHour;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceHolder extends PlaceholderExpansion{

    private HHPlugin plugin;
    public PlaceHolder(HHPlugin main){
        plugin = main;
    }
        
    @Override
    public String onRequest(OfflinePlayer player, String params) {

        switch (params) {
            case "actual":
                return plugin.getManager().getActiveHappyHours().stream().map(hh -> hh.getActualMode().name().toLowerCase()).toList().toString();

            case "next":
                return String.valueOf(plugin.getManager().getMinutesToNextHappyHour());
            
            case "left":
                for (HappyHour hh : plugin.getManager().getActiveHappyHours() ){
                    long left = hh.getDuration()*60000 - (System.currentTimeMillis() - hh.getActiveSince());
                    if (left > 0){
                        String name = hh.getDisplayName() + " "+ left/60000 + " min";
                        return name;
                    }
                }

            default:
                return "Invalid Placeholder";
        }
    }

    @Override
    public String getAuthor() {
        return "Askgaming";
    }

    @Override
    public String getIdentifier() {
        return "happyhour";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }
    

    
    
}
