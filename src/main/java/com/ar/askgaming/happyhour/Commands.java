package com.ar.askgaming.happyhour;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import com.ar.askgaming.happyhour.HHManager.Mode;

public class Commands implements TabExecutor {

    private HHPlugin plugin;
    public Commands(HHPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("happyhour").setExecutor(this);
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("start", "stop", "status", "help");
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("start")) {
                return List.of("mining", "fishing", "hunting", "experience", "woodcutting", "jobs", "votifier");
            }
            if (args[0].equalsIgnoreCase("stop")) {
                return plugin.getManager().getActiveHappyHours().stream().map(hh -> hh.getActualMode().name().toLowerCase()).toList();
            }
        }
        return null;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if (args.length == 0) {
            start(sender, args);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "start":
                start(sender,args);
                break;
            case "stop":
                stop(sender, args);
                break;
            case "status":
                status(sender, args);
                break;    
            case "reload":
                plugin.reloadConfig();
                sender.sendMessage("Config reloaded");
                break;
            case "help":
            default:
                help(sender, args);
                break;
        }

        return true;

    }
    private void start(CommandSender sender, String[] args){
        if (args.length == 1) {
            plugin.getManager().start();
            sender.sendMessage("You have started a random Happy Hour");
            return;
        }
        if (args.length == 2) {
            Mode mode;
            try {
                mode = Mode.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                sender.sendMessage("Invalid mode");
                return;
            }
            sender.sendMessage("You have started a Happy Hour with mode: " + mode.name());
            plugin.getManager().start(mode);
            return;
        }
    }
    private void stop(CommandSender sender, String[] args){
        if (args.length == 1) {
            plugin.getManager().stop();
            sender.sendMessage("You have stopped all Happy Hours");
            return;
        }
        if (args.length == 2){
            Mode mode;
            try {
                mode = Mode.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                sender.sendMessage("Invalid mode");
                return;
            }
            for (HappyHour hh : plugin.getManager().getActiveHappyHours()) {
                if (hh.getActualMode() == mode) {
                    plugin.getManager().stop(hh);
                    sender.sendMessage("You have stopped the Happy Hour with mode: " + mode.name());
                    return;
                }
            }
            sender.sendMessage("There is no Happy Hour active with mode: " + mode.name());
            return;
        }
    }
    private void status(CommandSender sender, String[] args){
        List <HappyHour> activeHappyHours = plugin.getManager().getActiveHappyHours();
        if (activeHappyHours.isEmpty()) {
            sender.sendMessage("There are no active Happy Hours");
            return;
        }else {
            for (HappyHour hh : activeHappyHours) {
                sender.sendMessage("Happy Hour with mode: " + hh.getActualMode().name() + " is active since: " + hh.getActiveSince());
            }
        }
        long minutesToNextHappyHour = plugin.getManager().getMinutesToNextHappyHour();
        sender.sendMessage("Minutes to next Happy Hour: " + minutesToNextHappyHour);
    }
    private void help(CommandSender sender, String[] args){
        sender.sendMessage("Happy Hour Plugin");
        sender.sendMessage("Usage: /happyhour [start|stop|status|help]");
        sender.sendMessage("start [mode]");
        sender.sendMessage("stop [mode]");

    }

}
