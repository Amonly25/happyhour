package com.ar.askgaming.happyhour.Misc;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.ar.askgaming.happyhour.HHPlugin;

public class HHScoreBoard {

    private Scoreboard board;

    Team mode;
    Team timeLeft;
    Team count;
    Team challenge;

    private HHPlugin plugin;
    public HHScoreBoard(HHPlugin plugin) {
        this.plugin = plugin;

        board = plugin.getServer().getScoreboardManager().getNewScoreboard();

        Objective obj = board.registerNewObjective("Happyhour", Criteria.DUMMY, plugin.getConfig().getString("scoreboard.title","§6HappyHour").replace('&', '§'));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        obj.getScore("§a").setScore(6);

        mode = board.registerNewTeam("mode");
        String text = plugin.getConfig().getString("scoreboard.mode","§7Mode: ").replace('&', '§');
        mode.addEntry(text);
        obj.getScore(text).setScore(5);

        timeLeft = board.registerNewTeam("timeLeft");
        String text2 = plugin.getConfig().getString("scoreboard.timeLeft","§7Time left: ").replace('&', '§');
        timeLeft.addEntry(text2);
        obj.getScore(text2).setScore(4);

        obj.getScore("").setScore(3);

        challenge = board.registerNewTeam("challenge");
        String text3 = plugin.getConfig().getString("scoreboard.challenge","§7Challenge: ").replace('&', '§');
        challenge.addEntry(text3);
        obj.getScore(text3).setScore(2);

        count = board.registerNewTeam("count");
        String text4 = plugin.getConfig().getString("scoreboard.count", "§7Count: ").replace('&', '§');
        count.addEntry(text4);
        obj.getScore(text4).setScore(1);

        setTimeLeft(60 + " min");
        
    }
    public void setMode(String mode) {
        this.mode.setSuffix(mode);
    }
    public void setTimeLeft(String timeLeft) {
        this.timeLeft.setSuffix(timeLeft);
    }
    public void setCount(String count) {
        this.count.setSuffix(count);
    }
    public void setChallenge(String challenge) {
        this.challenge.setSuffix(challenge);
    }

    public Scoreboard getBoard() {
        return board;
    }
    public void addPlayer(Player p) {
        Boolean b = plugin.getConfig().getBoolean("scoreboard.enable", true);
        if (!b) {
            return;
        }
        p.setScoreboard(board);
    }
    public void removePlayer(Player p) {
        p.setScoreboard(plugin.getServer().getScoreboardManager().getMainScoreboard());
    }
}
