package com.anton.mobilaapplikationer_uppgit3b;

/**
 * Created by Anton on 2016-12-06.
 */

public class SensorData {
    private int pleth;
    private int pulseRate;
    private long time;

    public SensorData(int pleth, int pulseRate) {
        this.pleth = pleth;
        this.pulseRate = pulseRate;
    }

    public int getPleth() {
        return pleth;
    }

    public int getPulseRate() {
        return pulseRate;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
