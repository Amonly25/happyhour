package com.ar.askgaming.happyhour.Challenges;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.ar.askgaming.happyhour.HHPlugin;
import com.ar.askgaming.happyhour.Challenges.ChallengeManager.Mode;

public class Commands implements TabExecutor {

    private ChallengeManager manager;
    private HHPlugin plugin;

    public Commands(HHPlugin plugin, ChallengeManager manager) {
        this.plugin = plugin;
        this.manager = manager;
        plugin.getServer().getPluginCommand("challenge").setExecutor(this);
    }

    private String getLang(String key, Player player) {
        return plugin.getLangManager().getLang(key, player);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return false;
        }
        switch (args[0].toLowerCase()) {
            case "start":
                start(sender, args);
                return true;
            case "add":
                add(sender, args);
                return true;
            case "list":
                list(sender,args);
                break;
            case "get":
                get(sender,args);
                break;
            case "reload":
                if (!sender.hasPermission("challenge.admin")) {
                    sender.sendMessage("You don't have permission to use this command");
                    return true;
                }
                manager.loadChallenges();
                sender.sendMessage("Lang reloaded");
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            list.add("list");
            list.add("get");
            if (sender.hasPermission("challenge.admin")) {
                list.add("start");
                list.add("add");
                list.add("reload");
            }
            return list;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("start")) {
            return List.of("global","race");    
        }
        return null;
    }
    //#region start
    private void start(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("Usage: /challenge start <global/race>");
            return;
        }
        if (!sender.hasPermission("challenge.admin")) {
            sender.sendMessage("You don't have permission to use this command");
            return;
        }

        Mode mode = manager.getRandomMode();

        switch (args[1].toLowerCase()) {
            case "global":
                manager.startGlobalChallenge(mode);
                break;
            case "race":
                manager.startRaceChallenge(mode);
                break;
            default:
                sender.sendMessage("Usage: /challenge start <global/race>");
        }
    }
    //#region add
    private void add(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("Usage: /challenge add <player>");
            return;
        }
        Player player = plugin.getServer().getPlayer(args[1]);
        if (player == null) {
            sender.sendMessage("Player not found");
            return;
        }
        if (!sender.hasPermission("challenge.admin")) {
            sender.sendMessage("You don't have permission to use this command");
            return;
        }
        Mode mode = manager.getRandomMode();
        manager.addSoloChallenge(player, mode);
        sender.sendMessage("Challenge added");
        
    }
    //#region list
    private void list(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command");
            return;
        }
        Player player = (Player) sender;
        if (args.length == 1) {
            player.sendMessage(getLang("challenge.available", player));
            List<Challenge> challenges = manager.getGlobalChallenges();
            if (!challenges.isEmpty()) {
                player.sendMessage(getLang("challenge.global_available", player));
                for (Challenge challenge : challenges) {
                    manager.sendChallegeHoverTextMessage(player, challenge);
                }
            }
            List<Challenge> raceChallenges = manager.getRaceChallenges();
            if (!raceChallenges.isEmpty()) {
                player.sendMessage(getLang("challenge.race_available", player));
                for (Challenge challenge : raceChallenges) {
                    manager.sendChallegeHoverTextMessage(player, challenge);
                }
            }
            List<Challenge> soloChallenges = manager.getSoloChallenges().get(player);
            if (soloChallenges != null && !soloChallenges.isEmpty()) {
                player.sendMessage(getLang("challenge.solo_available", player));
                for (Challenge challenge : soloChallenges) {
                    manager.sendChallegeHoverTextMessage(player, challenge);
                }
            }
        }
        if (args.length == 2) {
            
            return;
        }
    }
    //#region get   
    private void get(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command");
            return;
        }
        Player player = (Player) sender;
        if (args.length < 1) {
            player.sendMessage("Usage: /challenge get");
            return;
        }
        Integer current = manager.getSoloChallenges().getOrDefault(player, new ArrayList<>()).size();
        Integer max = manager.getMaxChallenges(player);
        if (current >= max) {
            player.sendMessage(plugin.getLangManager().getLang("challenge.max", player));
            return;
        }
        Mode mode = manager.getRandomMode();
        manager.addSoloChallenge(player, mode);
    }

}
