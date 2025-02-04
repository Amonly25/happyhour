package com.ar.askgaming.happyhour.Challenges;

import java.util.List;

import org.bukkit.Bukkit;

import com.ar.askgaming.happyhour.HHManager.Mode;

public class Challenge {

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

    public Mode getType() {
        return type;
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
    public void proccesRewards(){
        for (String reward : rewards) {
            Bukkit.getOnlinePlayers().forEach(p -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), reward.replace("%player%", p.getName())));
        }
    }

    public void reset() {
        progress = 0;
        completed = false;
    }
}
