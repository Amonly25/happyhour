package com.ar.askgaming.happyhour.ModesFromListeners;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.ar.askgaming.happyhour.HHPlugin;
import com.ar.askgaming.happyhour.HappyHour;
import com.ar.askgaming.happyhour.Managers.HHManager.Mode;

public class MiningWoodcuting implements Listener{

    private File file;
    private FileConfiguration config;
    private HHPlugin plugin;
    public MiningWoodcuting(HHPlugin plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        file = new File(plugin.getDataFolder(), "protectedBlocks.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        ConfigurationSection section = config.getConfigurationSection("protectedBlocks");
        if (section == null) {
            return;
        }
        for (String key : section.getKeys(false)) {
            Location loc = (Location) section.get(key);
            if (!loc.getBlock().isEmpty()) {
                protectedBlocks.add((Location) section.get(key));
                section.set(key, null);
                
            }
        }
    }

    private List<Location> protectedBlocks = new ArrayList<>();

    @EventHandler
    public void onMine(BlockDropItemEvent e){
        List<HappyHour> activeHappyHours = plugin.getManager().getActiveHappyHours();
        if (activeHappyHours.isEmpty()) {
            return;
        }
        for (HappyHour hh : activeHappyHours) {
            if (hh.getActualMode() == Mode.MINING || hh.getActualMode() == Mode.ALL) {
                processBlock(e, "modes.mining.items", "modes.mining.chance", "modes.mining.multiplier");
            }
            if (hh.getActualMode() == Mode.WOODCUTTING || hh.getActualMode() == Mode.ALL) {
                processBlock(e, "modes.woodcutting.items", "modes.woodcutting.chance", "modes.woodcutting.multiplier");
            }
        }
    }

    private void processBlock(BlockDropItemEvent e, String blocksKey, String chanceKey, String multiplierKey) {
        Block block = e.getBlock();
        if (protectedBlocks.contains(block.getLocation())) {
            Bukkit.broadcastMessage("multiplierKey");
            return;
        }
        Bukkit.broadcastMessage("fired");
        List<String> list = plugin.getConfig().getStringList(blocksKey);
        double chance = plugin.getConfig().getDouble(chanceKey);
        double multiplier = plugin.getConfig().getDouble(multiplierKey);

        List<Item> drops = e.getItems();

        if (Math.random() < chance) {
            Bukkit.broadcastMessage("Chance: " + chance);
            for (String blockName : list) {
                for (Item item : drops) {
                    Bukkit.broadcastMessage("comparing " + item.getItemStack().getType().toString() + " with " + blockName);
                    if (item.getItemStack().getType().name().equalsIgnoreCase(blockName)) {
                        Bukkit.broadcastMessage("same type");
                        for (int i = 1; i < multiplier; i++) {
                            block.getWorld().dropItem(block.getLocation(), item.getItemStack());
                            //DEBUG
                            Bukkit.broadcastMessage("Dropped item: " + item.getItemStack().getType().name());
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Block block = e.getBlock();
        List<String> blocks = plugin.getConfig().getStringList("modes.mining.blocks");
        List<String> woodcuttingBlocks = plugin.getConfig().getStringList("modes.woodcutting.blocks");
        if (blocks.contains(block.getType().name()) || woodcuttingBlocks.contains(block.getType().name())) {
            protectedBlocks.add(block.getLocation());
            config.set("protectedBlocks." + getXYZ(block.getLocation()), block.getLocation());
        }
        try {
            config.save(file);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    private String getXYZ(Location loc) {
        return loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }
}
