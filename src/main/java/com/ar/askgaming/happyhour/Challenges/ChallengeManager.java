package com.ar.askgaming.happyhour.Challenges;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.ar.askgaming.happyhour.HHManager.Mode;
import com.ar.askgaming.happyhour.HHPlugin;

public class ChallengeManager {

    private File file;
    private FileConfiguration config;
    
    private List<Challenge> challenges = new ArrayList<>();
    private List<Player> players = new ArrayList<>();

    private HHPlugin plugin;
    public ChallengeManager(HHPlugin plugin) {
        this.plugin = plugin;

        file = new File(plugin.getDataFolder(), "challenges.yml");
        reload();
    }
    public void reload() {

        if (!file.exists()) {
            plugin.saveResource("challenges.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(file);

        Set<String> keys = config.getKeys(false);
        if (keys.isEmpty()) {
            plugin.getLogger().severe("No challenges found in challenges.yml");
            return;
        }

        for (String path : keys) {
            Mode type;
            try {
                type = Mode.valueOf(config.getString(path + ".type"));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().severe("Invalid challenge type: " + config.getString(path + ".type"));
                continue;
            }
            int amount = config.getInt(path + ".amount",100);
            String name = config.getString(path + ".name","Challenge");
            List<String> rewards = config.getStringList(path + ".rewards");

            int players = Bukkit.getOnlinePlayers().size();

            challenges.add(new Challenge(type, amount+(players*amount), name, rewards));
        }
    }

    public Challenge getRandomChallenge(Mode type) {
        List<Challenge> challengesOfType = new ArrayList<>();
        for (Challenge challenge : challenges) {
            if (challenge.getType() == type) {
                challengesOfType.add(challenge);
            }
        }
        if (challengesOfType.isEmpty()) {
            return null;
        }
        return challengesOfType.get((int) (Math.random() * challengesOfType.size()));
    }

    private Challenge currentChallenge;
    public Challenge getCurrentChallenge() {
        return currentChallenge;
    }

    public List<Challenge> getChallenges() {
        return challenges;
    }

    public void startGlobalChallenge(Mode type) {
        currentChallenge = getRandomChallenge(type);
        if (currentChallenge == null) {
            plugin.getScoreBoard().setChallenge("-");
            plugin.getScoreBoard().setCount("-");
            plugin.getLogger().severe("No challenges found for type: " + type.name());
            return;
        }

        for (Player p : Bukkit.getOnlinePlayers()){
            p.sendMessage(plugin.getLangManager().getLang("challenge.start", p).replace("{name}", currentChallenge.getName()));
            p.sendMessage("");
        }
        currentChallenge.setProgress(0);
        plugin.getScoreBoard().setChallenge(currentChallenge.getName());
        plugin.getScoreBoard().setCount("0/"+currentChallenge.getAmount());
        
    }
    public void increaseProgress(Mode mode){
        if (getCurrentChallenge().getType() == mode) {

            Challenge challenge = getCurrentChallenge();

            if (challenge != null) {
                if (challenge.isCompleted()){
                    return;
                }
                int progress = challenge.getProgress();
                challenge.setProgress(progress + 1);
                // if (challenge.getPlayers()){

                // }
                plugin.getScoreBoard().setCount(progress + 1 + "/" + challenge.getAmount());

                if (challenge.getProgress() >= challenge.getAmount()) {
                    plugin.getScoreBoard().setCount("Completed");
                    challenge.proccesRewards();
                    challenge.setCompleted(true);
                }
            }
        }
    }  
}
