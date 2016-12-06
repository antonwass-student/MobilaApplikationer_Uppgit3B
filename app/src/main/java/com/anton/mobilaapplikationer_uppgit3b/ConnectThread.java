package com.anton.mobilaapplikationer_uppgit3b;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Anton on 2016-12-06.
 */
public class ConnectThread extends Thread{
    private static UUID MY_UUID;
    private BluetoothSocket socket = null;
    private BluetoothDevice device;
    private PlethDownloader plethDownloader;

    public ConnectThread(PlethDownloader pd, BluetoothDevice device, UUID uuid){
        this.plethDownloader = pd;
        this.MY_UUID = uuid;
        this.device = device;
        try{
            this.socket = device.createRfcommSocketToServiceRecord(MY_UUID);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        try{
            socket.connect();
            plethDownloader.onConnected(socket);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cancel(){
        try{
            socket.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}