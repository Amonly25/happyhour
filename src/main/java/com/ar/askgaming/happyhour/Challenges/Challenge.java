package com.ar.askgaming.happyhour.Challenges;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.ar.askgaming.happyhour.Challenges.ChallengeManager.Mode;
import com.ar.askgaming.happyhour.Challenges.ChallengeManager.Type;

public class Challenge implements ConfigurationSerializable{

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
    private long completedTime;

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
    @SuppressWarnings("unchecked")
    public Challenge(Map<String, Object> map) {
        this.mode = Mode.valueOf((String) map.get("mode"));
        this.type = Type.valueOf((String) map.get("type"));
        this.amount = (int) map.get("amount");
        this.progress = (int) map.get("progress");
        this.completed = (boolean) map.get("completed");
        this.name = (String) map.get("name");
        this.rewards = (List<String>) map.get("rewards");
        this.players = (List<Player>) map.get("players");
        this.decription = (String) map.get("description");

        Object entityType = map.get("entityType");
        if (entityType instanceof String) {
            this.entityType = EntityType.valueOf((String) entityType);
        } else {
            this.entityType = null;
        }
        
        Object material = map.get("material");
        if (material instanceof String) {
            this.material = Material.valueOf((String) material);
        } else {
            this.material = null;
        }
        this.completedTime = ((Number) map.get("completedTime")).longValue();
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
    public Integer increaseProgress(Player player) {
        if (playerProgress.containsKey(player.getUniqueId())) {
            int newProgress = playerProgress.get(player.getUniqueId()) + 1;
            playerProgress.put(player.getUniqueId(), newProgress);
            return newProgress;
        } else {
            playerProgress.put(player.getUniqueId(), 1);
            return 1;
        }
    }
    public String getWinningPlayer() {
        if (playerProgress.isEmpty()) {
            return "No one";
        }
    
        // Ordenamos el mapa por los valores (progreso) de mayor a menor
        Optional<Entry<UUID, Integer>> maxEntry = playerProgress.entrySet()
            .stream()
            .max(Entry.comparingByValue());
    
        // Verificamos si maxEntry está presente
        if (maxEntry.isPresent()) {
            String winner = Bukkit.getOfflinePlayer(maxEntry.get().getKey()).getName();
            String value = maxEntry.get().getValue().toString();
            return winner + ": " + value + "/" + amount;
        } else {
            return "No one";  // En caso de que maxEntry esté vacío
        }
    }
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("mode", mode.toString());
        map.put("type", type.toString());
        map.put("amount", amount);
        map.put("progress", progress);
        map.put("completed", completed);
        map.put("name", name);
        map.put("rewards", rewards);
        map.put("players", players);
        map.put("description", decription);
        map.put("entityType", entityType == null ? null : entityType.toString());
        map.put("material", material == null ? null : material.toString());
        map.put("completedTime", completedTime);
        return map;
    }
    public long getCompletedTime() {
        return completedTime;
    }
    public void setCompletedTime(long completedTime) {
        this.completedTime = completedTime;
    }
}
