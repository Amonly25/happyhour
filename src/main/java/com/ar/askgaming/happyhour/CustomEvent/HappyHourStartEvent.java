package com.ar.askgaming.happyhour.CustomEvent;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.ar.askgaming.happyhour.HHManager.Mode;
import com.ar.askgaming.happyhour.HappyHour;

public class HappyHourStartEvent extends Event {

    private HandlerList handlers = new HandlerList();

    public HappyHourStartEvent(HappyHour hh) {
        this.mode = hh.getActualMode();
        this.hh = hh;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    private Mode mode;
    private HappyHour hh;

    public HappyHour getHappyHour() {
        return hh;
    }

    public Mode getMode() {
        return mode;
    }
}
