package com.anton.mobilaapplikationer_uppgit3b;

import java.io.Serializable;

/**
 * Created by Anton on 2016-12-06.
 */

public class PlethData implements Serializable{
    private long time;
    private int value;

    public PlethData(long time, int value) {
        this.time = time;
        this.value = value;
    }

    public long getTime() {
        return time;
    }

    public int getValue() {
        return value;
    }
}
