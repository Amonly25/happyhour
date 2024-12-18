package com.ar.askgaming.happyhour;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

public class Commands implements TabExecutor {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if (args.length == 0) {
            sender.sendMessage("Use /happyhour help for more information");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "start":
                
                break;
            case "stop":
                
                break;
            case "help":
            default:
                break;
        }

        return true;

    }
}
