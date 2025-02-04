package com.ar.askgaming.happyhour.Misc;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.ar.askgaming.happyhour.HHPlugin;
import com.ar.askgaming.happyhour.HappyHour;

import net.md_5.bungee.api.ChatColor;

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
            return List.of("start", "stop", "status", "help","reload");
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("start")) {
                return List.of("mining", "fishing", "hunting_enemys","hunting_animals", "experience", "woodcutting", "jobs", "votifier", "all");
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
            status(sender, args);
            return true;
        }

        if (!sender.hasPermission("happyhour.admin")) {
            sender.sendMessage("§cYou don't have permission to use this command");
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
                sender.sendMessage("§aConfig reloaded");
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
            sender.sendMessage("§6You have started a random Happy Hour");
            return;
        }
        if (args.length == 2) {
            Mode mode;
            try {
                mode = Mode.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§cInvalid mode");
                return;
            }
            sender.sendMessage("§6You have started a Happy Hour with mode: " + mode.name());
            plugin.getManager().start(mode);
            return;
        }
    }
    private void stop(CommandSender sender, String[] args){
        if (args.length == 1) {
            plugin.getManager().stop();
            sender.sendMessage("§6You have stopped all Happy Hours");
            return;
        }
        if (args.length == 2){
            Mode mode;
            try {
                mode = Mode.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§cInvalid mode");
                return;
            }
            for (HappyHour hh : plugin.getManager().getActiveHappyHours()) {
                if (hh.getActualMode() == mode) {
                    plugin.getManager().stop(hh);
                    sender.sendMessage("§6You have stopped the Happy Hour with mode: " + mode.name());
                    return;
                }
            }
            sender.sendMessage("§cThere is no Happy Hour active with mode: " + mode.name());
            return;
        }
    }
    private void status(CommandSender sender, String[] args){
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }

        List <HappyHour> activeHappyHours = plugin.getManager().getActiveHappyHours();
        if (activeHappyHours.isEmpty()) {
            sender.sendMessage(plugin.getLangManager().getLang("no_active", player));
            return;
        }else {
            for (HappyHour hh : activeHappyHours) {
                    String name = plugin.getLangManager().getLang(hh.getActualMode().name().toLowerCase()+".name", player);
                    String descriptionMsg = plugin.getLangManager().getLang(hh.getActualMode().name().toLowerCase()+".description", player);
                    sender.sendMessage(plugin.getLangManager().getLang("start", player).replace("{mode}", name));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', descriptionMsg));
            }
        }
        long minutesToNextHappyHour = plugin.getManager().getMinutesToNextHappyHour();
        sender.sendMessage(plugin.getLangManager().getLang("next", player).replace("{minutes}", minutesToNextHappyHour+""));
    }
    private void help(CommandSender sender, String[] args){
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        sender.sendMessage(plugin.getLangManager().getLang("help", player));
    }
}
