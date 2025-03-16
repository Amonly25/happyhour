package com.ar.askgaming.happyhour.Challenges;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.ar.askgaming.happyhour.HHManager.Mode;
import com.ar.askgaming.happyhour.HHPlugin;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class Commands implements TabExecutor {

    private ChallengeManager manager;
    private HHPlugin plugin;

    public Commands(HHPlugin plugin, ChallengeManager manager) {
        this.plugin = plugin;
        this.manager = manager;
        plugin.getServer().getPluginCommand("challenge").setExecutor(this);
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
            default:
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("start", "add");
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

        Mode mode = plugin.getManager().getRandomMode();

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
        Mode mode = plugin.getManager().getRandomMode();
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
            player.sendMessage("Challenges availables:");
            List<Challenge> challenges = manager.getGlobalChallenges();
            if (!challenges.isEmpty()) {
                player.sendMessage("Global challenges:");
                for (Challenge challenge : challenges) {
                    sendChallegeHoverTextMessage(player, challenge);
                }
            }
            List<Challenge> raceChallenges = manager.getRaceChallenges();
            if (!raceChallenges.isEmpty()) {
                player.sendMessage("Race challenges:");
                for (Challenge challenge : raceChallenges) {
                    sendChallegeHoverTextMessage(player, challenge);
                }
            }
            List<Challenge> soloChallenges = manager.getSoloChallenges().get(player);
            if (soloChallenges != null && !soloChallenges.isEmpty()) {
                player.sendMessage("Solo challenges:");
                for (Challenge challenge : soloChallenges) {
                    sendChallegeHoverTextMessage(player, challenge);
                }
            }
        }
        if (args.length == 2) {
            
            return;
        }
    }
    //#region sendText
    private void sendChallegeHoverTextMessage(Player player, Challenge challenge) {
        TextComponent message = new TextComponent(challenge.getName());

        String click = " (Info)";

        TextComponent clickableText = new TextComponent(click);

        StringBuilder sb = new StringBuilder();
        sb.append(challenge.getDescription());
        sb.append("\n");
        String name = plugin.getLangManager().getLang(challenge.getMode().name().toLowerCase()+".name", player);
        sb.append("Mode: " + name);
        sb.append("\n");
        if (challenge.getEntityType() != null) {
            sb.append("Type: ").append(challenge.getEntityType()).append("\n");
        }
        if (challenge.getMaterial() != null) {
            sb.append("Material: ").append(challenge.getMaterial()).append("\n");
        }
        sb.append("Progress: " + challenge.getProgress() + "/" + challenge.getAmount());
        clickableText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(sb.toString())));

        message.addExtra(clickableText);

        player.spigot().sendMessage(message);
    }
}
