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
    private String displayName;
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

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
