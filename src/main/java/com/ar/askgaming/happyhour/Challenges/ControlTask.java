package com.ar.askgaming.happyhour.Challenges;

import java.util.Iterator;

import org.bukkit.scheduler.BukkitRunnable;

import com.ar.askgaming.happyhour.HHPlugin;

public class ControlTask extends BukkitRunnable{

    private HHPlugin plugin;
    private ChallengeManager challengeManager;
    public ControlTask(HHPlugin plugin, ChallengeManager challengeManager) {
        this.plugin = plugin;
        this.challengeManager = challengeManager;

        runTaskTimer(plugin, 20*60, 20*60);
    }

    @Override
    public void run() {
        long currentTime = System.currentTimeMillis();
        long newAfter = 24 * 60 * 60 * 1000;
        challengeManager.getSoloChallenges().forEach((k, v) -> {
            Iterator<Challenge> it = v.iterator();
            while (it.hasNext()) {
                Challenge c = it.next();
                if (!c.isCompleted()) {
                    continue;
                }
                if (currentTime - c.getCompletedTime() > newAfter) {
                    it.remove();
                    if (k != null) {
                        k.sendMessage(plugin.getLangManager().getLang("challenge.new_space", k));
                    }
                }
            }
        });
    }
}
