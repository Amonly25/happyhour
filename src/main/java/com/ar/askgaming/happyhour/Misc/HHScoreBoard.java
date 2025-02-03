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

        Objective obj = board.registerNewObjective("Happyhour", Criteria.DUMMY, "§a§lHappy Hour");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        obj.getScore("§a").setScore(6);

        mode = board.registerNewTeam("mode");
        mode.addEntry("§7Mode: ");
        obj.getScore("§7Mode: ").setScore(5);

        timeLeft = board.registerNewTeam("timeLeft");
        timeLeft.addEntry("§7Time left: ");
        obj.getScore("§7Time left: ").setScore(4);

        obj.getScore("").setScore(3);

        challenge = board.registerNewTeam("challenge");
        challenge.addEntry("§7Challenge: ");
        obj.getScore("§7Challenge: ").setScore(2);

        count = board.registerNewTeam("count");
        count.addEntry("§7Count: ");
        obj.getScore("§7Count: ").setScore(1);

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
        p.setScoreboard(board);
    }
    public void removePlayer(Player p) {
        p.setScoreboard(plugin.getServer().getScoreboardManager().getMainScoreboard());
    }
}
