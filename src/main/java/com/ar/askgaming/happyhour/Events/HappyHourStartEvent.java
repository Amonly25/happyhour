package com.ar.askgaming.happyhour.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.ar.askgaming.happyhour.HappyHour;
import com.ar.askgaming.happyhour.HHManager.Mode;

public class HappyHourStartEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public HappyHourStartEvent() {
    }

    private Mode mode;
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    private HappyHour hh;

    public void setHh(HappyHour hh) {
        this.hh = hh;
    }

    public HappyHour getHh() {
        return hh;
    }

    public HappyHour getHappyHour() {
        return hh;
    }

    public Mode getMode() {
        return mode;
    }

}
