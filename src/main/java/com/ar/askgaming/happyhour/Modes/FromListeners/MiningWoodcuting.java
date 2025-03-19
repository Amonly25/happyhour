package com.ar.askgaming.happyhour.Modes.FromListeners;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.ar.askgaming.happyhour.HHManager.Mode;
import com.ar.askgaming.happyhour.HHPlugin;
import com.ar.askgaming.happyhour.HappyHour;
import com.ar.askgaming.happyhour.Challenges.ChallengeManager;

public class MiningWoodcuting implements Listener{

    private static File file;
    private static FileConfiguration config;
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
        config = YamlConfiguration.loadConfiguration(file);
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

        Block block = e.getBlock();

        if (protectedBlocks.contains(block.getLocation())) {
            return;
        }

        List<HappyHour> activeHappyHours = plugin.getManager().getActiveHappyHours();
        if (activeHappyHours.isEmpty()) {
            return;
        }
        for (HappyHour hh : activeHappyHours) {
            Mode mode = hh.getActualMode();
            if (mode == Mode.MINING || mode == Mode.ALL) {
                processBlock(e, "modes.mining.items", "modes.mining.chance", "modes.mining.multiplier");
            }
            if (mode == Mode.WOODCUTTING || mode == Mode.ALL) {
                processBlock(e, "modes.woodcutting.items", "modes.woodcutting.chance", "modes.woodcutting.multiplier");
            }
        }
    }
    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (e.isCancelled()) {
            return;
        }
        Block block = e.getBlock();
        if (protectedBlocks.contains(block.getLocation())) {
            return;
        }
        Player p = e.getPlayer();
        checkBlockBreak(block.getType(), p);
    }
    private void checkBlockBreak(Material type, Player p) {

        List<String> mining = plugin.getConfig().getStringList("modes.mining.items");
        List<String> woodcutting = plugin.getConfig().getStringList("modes.woodcutting.items");
        for (String s : mining) {
            if (type.name().contains(s)) {
                plugin.getChallengeManager().increaseProgress(ChallengeManager.Mode.MINING, p, null, type);
            }
        }
        for (String s : woodcutting) {
            if (type.name().contains(s)) {
                plugin.getChallengeManager().increaseProgress(ChallengeManager.Mode.WOODCUTTING, p, null, type);
            }
        }
    }
    private void processBlock(BlockDropItemEvent e, String blocksKey, String chanceKey, String multiplierKey) {
        Block block = e.getBlock();

        List<String> list = plugin.getConfig().getStringList(blocksKey);
        double chance = plugin.getConfig().getDouble(chanceKey);
        double multiplier = plugin.getConfig().getDouble(multiplierKey);

        List<Item> drops = e.getItems();

        for (String blockName : list) {
            for (Item item : drops) {

                if (item.getItemStack().getType().name().equalsIgnoreCase(blockName)) {

                    if (Math.random() < chance) {
                        
                        for (int i = 1; i < multiplier; i++) {
                            block.getWorld().dropItem(block.getLocation(), item.getItemStack());
                            //DEBUG
                            //Bukkit.broadcastMessage("Dropped item: " + item.getItemStack().getType().name());
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Block block = e.getBlock();
        List<String> blocks = plugin.getConfig().getStringList("mining_types");
        List<String> woodcuttingBlocks = plugin.getConfig().getStringList("woodcutting_types");

        if (blocks.contains(block.getType().name()) || woodcuttingBlocks.contains(block.getType().name())) {
            protectedBlocks.add(block.getLocation());
            config.set("protectedBlocks." + getXYZ(block.getLocation()), block.getLocation());
        }
    }
    public static void saveData() {
        try {
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private String getXYZ(Location loc) {
        return loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }
}
