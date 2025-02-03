package com.ar.askgaming.happyhour.Challenges;

import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.ar.askgaming.happyhour.HHManager.Mode;

public class Challenge implements ConfigurationSerializable{

    private Mode type;
    private int amount;
    private int progress = 0;
    private boolean completed = false;
    private String name;

    private List<String> rewards;

    public Challenge(Mode type, int amount, String name, List<String> rewards) {
        this.type = type;
        this.amount = amount;
        this.name = name;
        this.rewards = rewards;
    }

    public Challenge(Map<String, Object> map) {
        this.type = Mode.valueOf((String) map.get("type"));
        this.amount = (int) map.get("amount");
        this.name = (String) map.get("name");
        this.rewards = (List<String>) map.get("rewards");
    }

    public Mode getType() {
        return type;
    }
    

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = Map.of(
            "type", type.name(),
            "amount", amount,
            "name", name,
            "rewards", rewards
        );
        return map;
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

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getRewards() {
        return rewards;
    }

    public void setRewards(List<String> rewards) {
        this.rewards = rewards;
    }
}
