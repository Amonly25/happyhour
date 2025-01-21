package com.ar.askgaming.happyhour.CustomEvent;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.ar.askgaming.happyhour.HappyHour;
import com.ar.askgaming.happyhour.Managers.HHManager.Mode;

public class HappyHourStartEvent extends Event {

    private HandlerList handlers = new HandlerList();

    public HappyHourStartEvent(HappyHour hh) {
        this.mode = hh.getActualMode();
        this.hh = hh;
    }

    private Mode mode;
    private HappyHour hh;

    public HappyHour getHappyHour() {
        return hh;
    }

    public Mode getMode() {
        return mode;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return new HandlerList();
    }
}
