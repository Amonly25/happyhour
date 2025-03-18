package com.ar.askgaming.happyhour.Challenges;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.ar.askgaming.happyhour.HHPlugin;

public class ChallengeManager {

    private File file, playerFile;
    private FileConfiguration config, playerConfig;
    
    private List<Challenge> baseChallenges = new ArrayList<>();

    private List<Challenge> globalChallenges = new ArrayList<>();
    private HashMap<Player, List<Challenge>> soloChallenges = new HashMap<>();
    private List<Challenge> raceChallenges = new ArrayList<>();

    public enum Type{
        GLOBAL,
        SOLO,
        RACE,
        UNASSIGNED
    }
    public enum Mode{
        MINING,
        WOODCUTTING,
        FISHING,
        HUNTING_ANIMALS,
        HUNTING_ENEMYS,
        VOTIFIER,
    }

    private HHPlugin plugin;
    public ChallengeManager(HHPlugin plugin) {
        this.plugin = plugin;

        file = new File(plugin.getDataFolder(), "challenges.yml");
        loadChallenges();

        new Commands(plugin, this);

        playerFile = new File(plugin.getDataFolder(), "data.yml");
        loadPlayerData();

        new ControlTask(plugin);
        
    }
    public void loadPlayerData() {
        if (!playerFile.exists()) {
            plugin.saveResource("data.yml", false);
        }
        playerConfig = YamlConfiguration.loadConfiguration(playerFile);
        Bukkit.getOnlinePlayers().forEach(p -> loadPlayerChallenges(p));
    }

    public void loadPlayerChallenges(Player player) {
        List<Challenge> loading = new ArrayList<>();
        ConfigurationSection section = playerConfig.getConfigurationSection(player.getUniqueId().toString());
        if (section != null) {
            Set<String> keys = section.getKeys(false);
            for (String key : keys) {
                ConfigurationSection challengeSection = section.getConfigurationSection(key);
                if (challengeSection == null) {
                    continue;
                }
                Challenge challenge = new Challenge(challengeSection.getValues(false));
                loading.add(challenge);
            }
        }

        soloChallenges.put(player, loading);
        int current = loading.size();
        int max = getMaxChallenges(player);
        if (current < max) {
            player.sendMessage(plugin.getLangManager().getLang("challenge.new_space", player));
        }
    }

    public void savePlayerData() {
        try {
            playerConfig.save(playerFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void savePlayerChallenges(Player player) {
        List<Challenge> list = soloChallenges.getOrDefault(player, new ArrayList<>());
        ConfigurationSection section = playerConfig.createSection(player.getUniqueId().toString());
        for (int i = 0; i < list.size(); i++) {
            section.createSection(i + "", list.get(i).serialize());
        }
    }

    //#region load
    public void loadChallenges() {

        if (!file.exists()) {
            plugin.saveResource("challenges.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(file);

        Set<String> keys = config.getKeys(false);
        if (keys.isEmpty()) {
            plugin.getLogger().severe("No challenges found in challenges.yml");
            return;
        }

        for (String path : keys) {
            Mode type;
            try {
                type = Mode.valueOf(config.getString(path + ".type"));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().severe("Invalid challenge type: " + config.getString(path + ".type"));
                continue;
            }
            int amount = config.getInt(path + ".amount",10);
            String name = config.getString(path + ".name","Challenge");
            String desc = config.getString(path + ".description","No description found");
            List<String> rewards = config.getStringList(path + ".rewards");

            baseChallenges.add(new Challenge(name, desc, type, amount, rewards, Type.UNASSIGNED, null, null, null));
        }
    }
    public Mode getRandomMode(){
        return Mode.values()[(int) (Math.random() * Mode.values().length)];
    }

    //#region startGlobal
    public void startGlobalChallenge(Mode type) {
        Challenge base = getRandomChallenge(type);
        if (base == null) {
            plugin.getLogger().severe("No challenges found for type: " + type.name());
            return;
        }
        List<Player> players = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(p -> {   
            players.add(p);
            p.sendMessage(plugin.getLangManager().getLang("challenge.start", p).replace("{name}", base.getName()));
            p.sendMessage("");
        });

        Challenge currentChallenge = new Challenge(base.getName(), base.getDescription(), base.getMode(), base.getAmount()*players.size(), base.getRewards(), Type.GLOBAL, players, null, null);
        globalChallenges.add(currentChallenge);

        plugin.getScoreBoard().setChallenge(currentChallenge.getName());
        plugin.getScoreBoard().setCount("0/"+currentChallenge.getAmount());
        
    }
    //#region startRace
    public void startRaceChallenge(Mode type) {
        Challenge base = getRandomChallenge(type);
        if (base == null) {
            plugin.getLogger().severe("No challenges found for type: " + type.name());
            return;
        }

        Bukkit.getOnlinePlayers().forEach(p -> {   
            p.sendMessage(plugin.getLangManager().getLang("challenge.race", p).replace("{name}", base.getName()));
            p.sendMessage("");
        });

        Challenge currentChallenge = new Challenge(base.getName(), base.getDescription(), base.getMode(), base.getAmount(), base.getRewards(), Type.RACE, new ArrayList<>(), getRandomEntityType(base.getMode()), getRandomMaterial(base.getMode()));
        raceChallenges.add(currentChallenge);
    }
    public void increaseProgress(Mode mode, Player player, EntityType entityType, Material material) {
        checkGlobalChallenges(mode, player, entityType, material);
        checkSoloChallenges(mode, player, entityType, material);
        checkRaceChallenges(mode, player, entityType, material);
    }  
    //#region add
    public void addSoloChallenge(Player player, Mode mode) {
        List<Challenge> challenges = soloChallenges.getOrDefault(player, new ArrayList<>());
        Challenge base = getRandomChallenge(mode);
        if (base == null) {
            plugin.getLogger().severe("No challenges found for type: " + plugin.getManager().getRandomMode().name());
            return;
        }
        List<Player> players = new ArrayList<>();
        players.add(player);
        Challenge newChallenge = new Challenge(base.getName(), base.getDescription(), base.getMode(), base.getAmount(), base.getRewards(), Type.SOLO, players, getRandomEntityType(base.getMode()), getRandomMaterial(base.getMode()));
        challenges.add(newChallenge);
        soloChallenges.put(player, challenges);

        player.sendMessage(plugin.getLangManager().getLang("challenge.solo", player).replace("{name}", newChallenge.getName()));
        player.sendMessage("");
    }
    //#region check
    private void checkGlobalChallenges(Mode mode, Player player, EntityType entityType, Material material) {
        if (globalChallenges.isEmpty()) {
            return;
        }
        
        Iterator<Challenge> iterator = globalChallenges.iterator();
        while (iterator.hasNext()) {
            Challenge challenge = iterator.next();
            plugin.getLogger().info("Challenge: " + challenge.getName());
            if (challenge.isCompleted() || challenge.getMode() != mode || !challenge.getPlayers().contains(player)) {
                continue;
            }
            
            challenge.setProgress(challenge.getProgress() + 1);
            player.sendMessage(plugin.getLangManager().getLang("challenge.progress", player).replace("{name}", challenge.getName()).replace("{progress}", challenge.getProgress() + "/" + challenge.getAmount()));
            if (challenge.getProgress() >= challenge.getAmount()) {
                challenge.setCompleted(true);
                challenge.proccesRewards();
                iterator.remove();
                plugin.getScoreBoard().setChallenge("");
                plugin.getScoreBoard().setCount("");
            } else {
                plugin.getScoreBoard().setCount(challenge.getProgress() + "/" + challenge.getAmount());
            }
        }
    }
    private void checkSoloChallenges(Mode mode, Player player, EntityType entityType, Material material) {
        List<Challenge> playerChallenges = soloChallenges.getOrDefault(player, new ArrayList<>());
        if (playerChallenges.isEmpty()) {
            plugin.getLogger().info("No solo challenges found for player: " + player.getName());
            return;
        }
        Iterator<Challenge> iterator = playerChallenges.iterator();
        while (iterator.hasNext()) {
            Challenge challenge = iterator.next();
            if (challenge.isCompleted() || challenge.getMode() != mode) continue;
            plugin.getLogger().info("Challenge: " + challenge.getName());
    
            if (validateChallengeType(challenge, mode, entityType, material)) {
                challenge.setProgress(challenge.getProgress() + 1);
                plugin.getLogger().info("Progress: " + challenge.getProgress());
                player.sendMessage(plugin.getLangManager().getLang("challenge.progress", player).replace("{name}", challenge.getName()).replace("{progress}", challenge.getProgress() + "/" + challenge.getAmount()));
            }
    
            if (challenge.getProgress() >= challenge.getAmount()) {
                plugin.getLogger().info("Challenge completed");
                challenge.setCompleted(true);
                challenge.proccesRewards();
                player.sendMessage(plugin.getLangManager().getLang("challenge.completed", player).replace("{name}", challenge.getName()));
            }
        }
    }
    private void checkRaceChallenges(Mode mode, Player player, EntityType entityType, Material material) {
        if (raceChallenges.isEmpty()) {
            return;
        }
        Iterator<Challenge> iterator = raceChallenges.iterator();
        while (iterator.hasNext()) {
            Challenge challenge = iterator.next();
            if (challenge.isCompleted() || challenge.getMode() != mode) continue;
            plugin.getLogger().info("Challenge: " + challenge.getName());
            if (validateChallengeType(challenge, mode, entityType, material)) {
                Integer progress = challenge.increaseProgress(player);
                player.sendMessage(plugin.getLangManager().getLang("challenge.progress", player).replace("{name}", challenge.getName()).replace("{progress}", progress + "/" + challenge.getAmount()));
            }    
    
            if (challenge.getPlayerProgress().getOrDefault(player, 0) >= challenge.getAmount()) {
                challenge.setCompleted(true);
                challenge.getPlayers().add(player);
                challenge.proccesRewards();
                Bukkit.getOnlinePlayers().forEach(pl ->
                    pl.sendMessage(plugin.getLangManager().getLang("challenge.completed", pl).replace("{name}", challenge.getName()))
                );
                iterator.remove();
            }
        }
    }
    private boolean validateChallengeType(Challenge challenge, Mode mode, EntityType entityType, Material material) {
        switch (mode) {
            case MINING:
            case WOODCUTTING:
                return challenge.getMaterial() == null || challenge.getMaterial() == material;
            case FISHING:
            case HUNTING_ANIMALS:
            case HUNTING_ENEMYS:
                return challenge.getEntityType() == null || challenge.getEntityType() == entityType;
            default:
                return true;
        }
    }
    //#region getRandom
    public Challenge getRandomChallenge(Mode mode) {
        List<Challenge> challengesOfType = new ArrayList<>();
        for (Challenge challenge : baseChallenges) {
            if (challenge.getMode() == mode) {
                challengesOfType.add(challenge);
            }
        }
        if (challengesOfType.isEmpty()) {
            return null;
        }
        return challengesOfType.get((int) (Math.random() * challengesOfType.size()));
    }
    public EntityType getRandomEntityType(Mode mode) {
        if (mode == Mode.HUNTING_ANIMALS) {
            EntityType[] animals = new EntityType[] {EntityType.COW, EntityType.PIG, EntityType.SHEEP, EntityType.CHICKEN,
                 EntityType.RABBIT};
            return animals[(int) (Math.random() * animals.length)];

        }
        if (mode == Mode.HUNTING_ENEMYS) {
            EntityType[] enemys = new EntityType[] {EntityType.ZOMBIE, EntityType.SKELETON, EntityType.CREEPER, EntityType.SPIDER, EntityType.ENDERMAN
                    , EntityType.BLAZE, EntityType.GHAST, EntityType.GUARDIAN, EntityType.SLIME, EntityType.MAGMA_CUBE, EntityType.WITHER_SKELETON,
                    EntityType.SHULKER, EntityType.STRAY, EntityType.HUSK, EntityType.PILLAGER};
            return enemys[(int) (Math.random() * enemys.length)];
        }
        return null;
    }
    public Material getRandomMaterial(Mode mode) {
        String key = mode.toString().toLowerCase();
        List<String> materials = plugin.getConfig().getStringList("modes."+key+".items");

        if (materials.isEmpty()) {
            return null;
        }
        try {
            return Material.valueOf(materials.get((int) (Math.random() * materials.size())));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    //#region getters
    public List<Challenge> getBaseChallenges() {
        return baseChallenges;
    }
    public List<Challenge> getGlobalChallenges() {
        return globalChallenges;
    }
    public HashMap<Player, List<Challenge>> getSoloChallenges() {
        return soloChallenges;
    }
    public List<Challenge> getRaceChallenges() {
        return raceChallenges;
    }
    public Integer getMaxChallenges(Player player) {
        int actual = 0;
        for (int i = 0; i < 100; i++) {
            String permission = "challenges.max." + i;
            if (player.hasPermission(permission)) {
                actual = i;
            }
        }
        return actual;
    }
}
