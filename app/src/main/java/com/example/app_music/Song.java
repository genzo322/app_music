package com.example.app_music;

import java.io.Serializable;

public class Song implements Serializable {
    private String name;
    private int resouce;

    public Song(String name, int resouce) {
        this.name = name;
        this.resouce = resouce;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResouce() {
        return resouce;
    }

    public void setResouce(int resouce) {
        this.resouce = resouce;
    }
}
