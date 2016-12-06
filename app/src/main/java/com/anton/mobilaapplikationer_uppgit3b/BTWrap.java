package com.anton.mobilaapplikationer_uppgit3b;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Anton on 2016-12-06.
 */

public class BTWrap {

    BluetoothDevice device;

    public BTWrap(BluetoothDevice device){
        this.device = device;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    @Override
    public String toString(){
        return device.getName();
    }
}
