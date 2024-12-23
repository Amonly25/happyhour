package com.ar.askgaming.happyhour.Managers;

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

import com.ar.askgaming.happyhour.HHPlugin;
import com.ar.askgaming.happyhour.HappyHour;
import com.ar.askgaming.happyhour.CustomEvent.HappyHourStartEvent;

public class HHManager extends BukkitRunnable{

    private HHPlugin plugin;
    public HHManager(HHPlugin plugin) {
        this.plugin = plugin;

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
        hh.setDisplayName(displayName);
        HappyHourStartEvent event = new HappyHourStartEvent(hh);
        Bukkit.getPluginManager().callEvent(event);
        
        activeHappyHours.add(hh);
        for (Player pl : Bukkit.getOnlinePlayers()){
            String name = plugin.getLangManager().getLang(mode.name().toLowerCase()+".name", pl);
            pl.sendMessage(plugin.getLangManager().getLang("start", pl).replace("{mode}", name));
        }        
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
        }
        hh = null;
    }
    public void stop(){
        for (HappyHour hh : activeHappyHours) {
            stop(hh);
        }
    }

    private Mode getRandomMode() {
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

    @Override
    public void run() {

        checkTimeForScheduler();

        if (activeHappyHours.isEmpty()){
            return;
        }
        for (HappyHour hh : activeHappyHours) {
            if (hh.isActive()) {
                if (System.currentTimeMillis() - hh.getActiveSince() > hh.getDuration()*60000) {
                    stop(hh);
                }
            }
        }
    }

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
