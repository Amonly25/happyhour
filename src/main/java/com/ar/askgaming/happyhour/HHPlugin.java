package com.ar.askgaming.happyhour;

import org.bukkit.plugin.java.JavaPlugin;

import com.ar.askgaming.happyhour.Managers.HHManager;
import com.ar.askgaming.happyhour.Managers.LangManager;
import com.ar.askgaming.happyhour.Misc.Commands;
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
        langManager = new LangManager(this);

        new Experience(this);
        new Fishing(this);
        new Hunting(this);
        new MiningWoodcuting(this);

        if (getServer().getPluginManager().getPlugin("Jobs") != null) {
            new Jobs(this);
        }
        if (getServer().getPluginManager().getPlugin("VotifierPlus") != null) {
            new Votifier(this);
        }

        new Commands(this);

    }
    public void onDisable(){

    }
    private HHManager manager;
    private LangManager langManager;
    
    public LangManager getLangManager() {
        return langManager;
    }
    public void setManager(HHManager manager) {
        this.manager = manager;
    }
    public HHManager getManager() {
        return manager;
    }


}
