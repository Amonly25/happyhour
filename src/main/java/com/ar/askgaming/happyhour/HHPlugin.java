package com.ar.askgaming.happyhour;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.ar.askgaming.happyhour.Challenges.ChallengeManager;
import com.ar.askgaming.happyhour.Listeners.PlayerJoinQuitListener;
import com.ar.askgaming.happyhour.Misc.HHScoreBoard;
import com.ar.askgaming.happyhour.Misc.Language;
import com.ar.askgaming.happyhour.Misc.PlaceHolder;
import com.ar.askgaming.happyhour.Modes.FromIntegrations.Jobs;
import com.ar.askgaming.happyhour.Modes.FromIntegrations.Votifier;
import com.ar.askgaming.happyhour.Modes.FromListeners.Experience;
import com.ar.askgaming.happyhour.Modes.FromListeners.Fishing;
import com.ar.askgaming.happyhour.Modes.FromListeners.Hunting;
import com.ar.askgaming.happyhour.Modes.FromListeners.MiningWoodcuting;

public class HHPlugin extends JavaPlugin{

    private static HHPlugin instance;

    public void onEnable(){

        instance = this;
        saveDefaultConfig();

        manager = new HHManager();
        langManager = new Language(this);
        scoreBoard = new HHScoreBoard(this);
        challengeManager = new ChallengeManager(this);
        
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
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceHolder(this).register();
        }

        new Commands(this);

        new PlayerJoinQuitListener(this);
    }
    public void onDisable(){
        for (Player pl : Bukkit.getOnlinePlayers()){
            scoreBoard.removePlayer(pl);
            challengeManager.savePlayerChallenges(pl);
        }
        challengeManager.savePlayerData();
        MiningWoodcuting.saveData();
        
    }
    private HHManager manager;
    private Language langManager;
    private HHScoreBoard scoreBoard;
    private ChallengeManager challengeManager;
    
    public ChallengeManager getChallengeManager() {
        return challengeManager;
    }
    public HHScoreBoard getScoreBoard() {
        return scoreBoard;
    }
    public Language getLangManager() {
        return langManager;
    }
    public HHManager getManager() {
        return manager;
    }
    public static HHPlugin getInstance() {
        return instance;
    }
}
