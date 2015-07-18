package com.wyvern.fun.magicconch.Model;

import java.util.Date;

/**
 * Created by gyosh on 7/18/15.
 */
public class Category {
    private int id;
    private String name;
    private String lastAccess; // I know Date, but here we don't need that

    public Category(int id, String name, String lastAccess) {
        this.id = id;
        this.name = name;
        this.lastAccess = lastAccess;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastAccess() {
        return lastAccess;
    }
}
