package com.wyvern.fun.magicconch.Model;

/**
 * Created by gyosh on 7/18/15.
 */
public class Option {
    private int id;
    private String name;
    private boolean enabled;

    public Option(int id, String name, boolean enabled) {
        this.id = id;
        this.name = name;
        this.enabled = enabled;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setName(String name) {
        this.name = name;
    }
}
