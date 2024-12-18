package com.ar.askgaming.happyhour;

import org.bukkit.plugin.java.JavaPlugin;

import com.ar.askgaming.happyhour.ModesFromIntegrations.Jobs;
import com.ar.askgaming.happyhour.ModesFromIntegrations.Votifier;
import com.ar.askgaming.happyhour.ModesFromListeners.Experience;
import com.ar.askgaming.happyhour.ModesFromListeners.Fishing;
import com.ar.askgaming.happyhour.ModesFromListeners.Hunting;
import com.ar.askgaming.happyhour.ModesFromListeners.MiningWoodcuting;


public class HHPlugin extends JavaPlugin{

    public void onEnable(){
        saveDefaultConfig();

        manager = new HHManager(this);

        new Jobs(this);
        new Votifier(this);
        new Experience(this);
        new Fishing(this);
        new Hunting(this);
        new MiningWoodcuting(this);

    }
    public void onDisable(){

    }
    private HHManager manager;
    
    public void setManager(HHManager manager) {
        this.manager = manager;
    }
    public HHManager getManager() {
        return manager;
    }


}
