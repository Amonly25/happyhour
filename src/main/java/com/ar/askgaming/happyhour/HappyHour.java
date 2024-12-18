package com.ar.askgaming.happyhour;

import com.ar.askgaming.happyhour.HHManager.Mode;

public class HappyHour{

    public HappyHour(Mode mode, long duration) {
        activeSince = System.currentTimeMillis();
        this.actualMode = mode;
        this.duration = duration;
    }

    private long duration;
    private long activeSince;
    private Mode actualMode;
    private boolean active = true;

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public long getDuration() {
        return duration;
    }

    public long getActiveSince() {
        return activeSince;
    }

    public Mode getActualMode() {
        return actualMode;
    }

}
