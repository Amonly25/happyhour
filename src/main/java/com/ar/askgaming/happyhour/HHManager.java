package com.ar.askgaming.happyhour;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.ar.askgaming.happyhour.Challenges.ChallengeManager;
import com.ar.askgaming.happyhour.Events.HappyHourStartEvent;

public class HHManager extends BukkitRunnable{

    private final HHPlugin plugin;

    public HHManager() {
        this.plugin = HHPlugin.getInstance();

        runTaskTimer(plugin, 0, 20*60);
    }
    public enum Mode {
        MINING,
        FISHING,
        HUNTING_ANIMALS,
        HUNTING_ENEMYS,
        EXPERIENCE,
        WOODCUTTING,
        JOBS,
        VOTIFIER,
        ALL,
    }

    private final CopyOnWriteArrayList<HappyHour> activeHappyHours = new CopyOnWriteArrayList<>();

    public List<HappyHour> getActiveHappyHours() {
        return activeHappyHours;
    }

    //#region start
    public void start(Mode mode, long duration) {
        if (mode == null) {
            mode = getRandomMode();
        }

        if (duration == 0) {
            // set dafeault value
            duration = plugin.getConfig().getLong("duration_in_minutes",60);
        }

        HappyHour hh = new HappyHour(mode, duration);

        String displayName = plugin.getLangManager().getLang(mode.name().toLowerCase()+".name", null);
        String description = plugin.getLangManager().getLang(mode.name().toLowerCase()+".description", null);
        plugin.getScoreBoard().setMode(displayName);
        hh.setDisplayName(displayName);
        hh.setDescription(description);

        HappyHourStartEvent event = new HappyHourStartEvent(hh);
        Bukkit.getPluginManager().callEvent(event);
        
        activeHappyHours.add(hh);

        try {
            plugin.getChallengeManager().startGlobalChallenge(ChallengeManager.Mode.valueOf(mode.name()));
        } catch (Exception e) {
            plugin.getLogger().severe("No challenge found for mode " + mode.name());
        }
        plugin.getLogger().info("Happy hour started: " + hh.getDisplayName());      
    }

    public void start(){
        start(null, 0);
    }
    public void start(Mode mode) {
        start(mode, 0);
    }
    //#region stop
    public void stop(HappyHour hh) {
        hh.setActive(false);

        activeHappyHours.remove(hh);
        for (Player pl : Bukkit.getOnlinePlayers()){
            String name = plugin.getLangManager().getLang(hh.getActualMode().name().toLowerCase()+".name", pl);
            pl.sendMessage(plugin.getLangManager().getLang("stop", pl).replace("{mode}", name));
            plugin.getScoreBoard().removePlayer(pl);
        }
        hh = null;
    }
    public void stop(){
        for (HappyHour hh : activeHappyHours) {
            stop(hh);
        }
    }
    //#region getRandomMode
    public Mode getRandomMode() {
        List<String> modes = plugin.getConfig().getStringList("enabled_types");
        if (modes.isEmpty()) {
            plugin.getLogger().severe("No enabled types found in config");
        }
        try {
            return Mode.valueOf(modes.get((int) (Math.random() * modes.size())).toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLogger().severe("Invalid mode in config");
            
        }
        
        return Mode.values()[(int) (Math.random() * Mode.values().length)];
    }

    private long broadcastCooldown = 0;

    //#region run
    @Override
    public void run() {

        checkTimeForScheduler();

        if (activeHappyHours.isEmpty()){
            return;
        }
        long currentTime = System.currentTimeMillis();
        for (HappyHour hh : activeHappyHours) {
            if (hh.isActive()) {

                long left = hh.getDuration()*60000 - (System.currentTimeMillis() - hh.getActiveSince());
                String timeleft = left/60000 + " min";
                plugin.getScoreBoard().setTimeLeft(timeleft);

                if (currentTime - broadcastCooldown > 1000*60*10) {
                    for (Player pl : Bukkit.getOnlinePlayers()){
                        String name = plugin.getLangManager().getLang(hh.getActualMode().name().toLowerCase()+".name", pl);
                        pl.sendMessage(plugin.getLangManager().getLang("status", pl).replace("{mode}", name));
                    }
                    broadcastCooldown = currentTime;
                }

                if (System.currentTimeMillis() - hh.getActiveSince() > hh.getDuration()*60000) {
                    stop(hh);
                }
            }
        }
    }
    //#region scheduler
    private void checkTimeForScheduler() {

        ConfigurationSection scheduler = plugin.getConfig().getConfigurationSection("scheduler");
        if (scheduler == null) {
            plugin.getLogger().severe("No scheduler section found in config");
            return;
        }

        String currentDay = java.time.LocalDate.now().getDayOfWeek().name().toLowerCase();
        String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

        ConfigurationSection daySection = scheduler.getConfigurationSection(currentDay);
        if (daySection != null) {
            Set<String> times = daySection.getKeys(false);
            for (String time : times) {
                if (time.equals(currentTime)) {
                    List<String> modes = daySection.getStringList(time);
                    for (String mode : modes) {
                        if (mode.equalsIgnoreCase("random")) {
                            plugin.getLogger().info("Starting random mode on " + currentDay + " at " + currentTime);
                            start(getRandomMode());
                        }else {
                            try {
                                Mode m = Mode.valueOf(mode.toUpperCase());
                                plugin.getLogger().info("Starting mode " + m.name() + " on " + currentDay + " at " + currentTime);
                                start(m);
                            } catch (IllegalArgumentException e) {
                                plugin.getLogger().severe("Invalid mode " + mode);
                            }
                        }
                    }
                }
            }
        }
    }
    //#region getNext
    public long getMinutesToNextHappyHour() {
        ConfigurationSection scheduler = plugin.getConfig().getConfigurationSection("scheduler");
        if (scheduler == null) {
            plugin.getLogger().severe("No scheduler section found in config");
            return -1;
        }
        LocalDateTime now = LocalDateTime.now();
        List<LocalDateTime> happyHours = new ArrayList<>();

        for (String day : scheduler.getKeys(false)) {
            ConfigurationSection daySection = scheduler.getConfigurationSection(day);
            if (daySection != null) {
                for (String time : daySection.getKeys(false)) {
                    LocalTime localTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
                    DayOfWeek dayOfWeek = DayOfWeek.valueOf(day.toUpperCase());
                    LocalDate nextHappyHourDate = now.with(TemporalAdjusters.nextOrSame(dayOfWeek)).toLocalDate();
                    LocalDateTime happyHour = LocalDateTime.of(nextHappyHourDate, localTime);
                    if (happyHour.isBefore(now)) {
                        happyHour = happyHour.plusWeeks(1);
                    }
                    happyHours.add(happyHour);
                }
            }
        }

        happyHours.sort(LocalDateTime::compareTo);

        if (!happyHours.isEmpty()) {
            LocalDateTime nextHappyHour = happyHours.get(0);
            return ChronoUnit.MINUTES.between(now, nextHappyHour);
        }

        return -1; // Indica que no se encontró ningún happy hour programado
    }
}
