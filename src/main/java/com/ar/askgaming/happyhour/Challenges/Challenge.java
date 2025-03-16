package com.ar.askgaming.happyhour.Challenges;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.ar.askgaming.happyhour.HHManager.Mode;
import com.ar.askgaming.happyhour.Challenges.ChallengeManager.Type;

public class Challenge {

    private Mode mode;
    private Type type;
    private int amount;
    private int progress = 0;
    private boolean completed = false;
    private String name;
    private EntityType entityType;
    private Material material;
    private List<String> rewards;
    private List<Player> players;
    private String decription;
    private HashMap<UUID, Integer> playerProgress = new HashMap<>();

    public Challenge(String displayName, String desc, Mode mode, int amount, List<String> rewards, Type type, List<Player> players, EntityType entityType, Material material) {
        this.decription = desc;
        this.mode = mode;
        this.amount = amount;
        this.name = displayName;
        this.rewards = rewards;
        this.type = type;
        this.players = players;
        this.entityType = entityType;
        this.material = material;
    }
    public void proccesRewards(){
        for (String reward : rewards) {
            for (Player p : players) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), reward.replace("%player%", p.getName()));
            }
        }
    }

    public List<Player> getPlayers() {
        return players;
    }
    public Mode getMode() {
        return mode;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getName() {
        return name;
    }

    public List<String> getRewards() {
        return rewards;
    }

    public Type getType() {
        return type;
    }
    public EntityType getEntityType() {
        return entityType;
    }
    public Material getMaterial() {
        return material;
    }
    public String getDescription() {
        return decription;
    }
    public HashMap<UUID, Integer> getPlayerProgress() {
        return playerProgress;
    }
    public void increaseProgress(Player player) {
        if (playerProgress.containsKey(player.getUniqueId())) {
            playerProgress.put(player.getUniqueId(), playerProgress.get(player.getUniqueId()) + 1);
        } else {
            playerProgress.put(player.getUniqueId(), 1);
        }
    }
}
