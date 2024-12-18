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

import com.ar.askgaming.happyhour.HHManager.Mode;
import com.ar.askgaming.happyhour.HHPlugin;
import com.ar.askgaming.happyhour.HappyHour;

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
            if (hh.getActualMode() == Mode.MINING) {
                processBlock(e, "happyhour.mining.blocks", "happyhour.mining.chance", "happyhour.mining.multiplier");
            } else if (hh.getActualMode() == Mode.WOODCUTTING) {
                processBlock(e, "happyhour.woodcutting.blocks", "happyhour.woodcutting.chance", "happyhour.woodcutting.multiplier");
            }
        }
    }

    private void processBlock(BlockDropItemEvent e, String blocksKey, String chanceKey, String multiplierKey) {
        Block block = e.getBlock();
        if (protectedBlocks.contains(block.getLocation())) {
            return;
        }
        List<String> drops = plugin.getConfig().getStringList(blocksKey);
        for (String drop : drops) {
            if (block.getType().name().equalsIgnoreCase(drop)) {
                double chance = plugin.getConfig().getDouble(chanceKey);
                double multiplier = plugin.getConfig().getDouble(multiplierKey);
                if (Math.random() < chance) {
                    for (int i = 1; i < multiplier; i++) {
                        for (Item item : e.getItems()) {
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
        List<String> blocks = plugin.getConfig().getStringList("happyhour.mining.blocks");
        List<String> woodcuttingBlocks = plugin.getConfig().getStringList("happyhour.woodcutting.blocks");
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
