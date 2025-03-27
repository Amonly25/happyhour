package com.ar.askgaming.happyhour.Events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.ar.askgaming.happyhour.HHManager.Mode;
import com.ar.askgaming.happyhour.HHPlugin;
import com.ar.askgaming.happyhour.HappyHour;
import com.ar.askgaming.universalnotifier.UniversalNotifier;
import com.ar.askgaming.universalnotifier.Managers.AlertManager.Alert;

import net.md_5.bungee.api.ChatColor;

public class HappyHourStartEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final HHPlugin plugin = HHPlugin.getInstance();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public HappyHourStartEvent(HappyHour happyHour) {
        this.happyHour = happyHour;
        Mode mode = happyHour.getActualMode();
        
        for (Player pl : Bukkit.getOnlinePlayers()){
            plugin.getScoreBoard().addPlayer(pl);
            String name = plugin.getLangManager().getLang(mode.name().toLowerCase()+".name", pl);
            String descriptionMsg = plugin.getLangManager().getLang(mode.name().toLowerCase()+".description", pl);
            pl.sendMessage("");
            pl.sendMessage(plugin.getLangManager().getLang("start", pl).replace("{mode}", name));
            pl.sendMessage(ChatColor.translateAlternateColorCodes('&', descriptionMsg));
        }

        if (plugin.getServer().getPluginManager().getPlugin("UniversalNotifier") != null) {
            UniversalNotifier notifier = UniversalNotifier.getInstance();
            String start = plugin.getConfig().getString("notifier.start", "⏰ Happy Hour started!").replace("%mode%", happyHour.getDisplayName());
            notifier.getNotificationManager().broadcastToAll(Alert.CUSTOM, start);
        } 

    }
    private HappyHour happyHour;

    public HappyHour getHappyHour() {
        return happyHour;
    }

}
