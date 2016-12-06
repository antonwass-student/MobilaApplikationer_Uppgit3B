package com.anton.mobilaapplikationer_uppgit3b;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;

import java.util.Set;
import java.util.UUID;

/**
 * Created by Anton on 2016-12-06.
 */

public class PlethDownloader {

    private final static int REQUEST_ENABLE_BT = 1;
    private MainActivity activity;
    private BluetoothDevice selectedDevice;

    private ConnectThread connectThread;
    private SocketHandler socketHandler;

    public PlethDownloader(MainActivity activity) throws BluetoothNotSupportedException{
        this.activity = activity;

        BluetoothAdapter adapter
                = BluetoothAdapter.getDefaultAdapter();

        if(adapter == null) {
            throw new BluetoothNotSupportedException("Bluetooth is not supported");
        }

        //bluetooth is supported.

        if(adapter.isEnabled() == false){
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            activity.startActivityForResult(intent, REQUEST_ENABLE_BT);
        }

        Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();

        for(BluetoothDevice device : pairedDevices){
            if(device.getName().contains("Nonin")){
                selectedDevice = device;
                break;
            }
        }
    }

    /**
     * Creates a thread that attempts to connect to the selected bt device
     */
    public void connectToDevice(){
        connectThread =
                new ConnectThread(
                        this,
                        selectedDevice,
                        UUID.fromString("490dc632-bbbc-11e6-a4a6-cec0c932ce01")
                );

        connectThread.start();
    }

    /**
     * closes all threads.
     */
    public void cancel(){
        if(connectThread!=null)
            connectThread.cancel();
        if(socketHandler!=null)
            socketHandler.cancel();
    }

    /**
     * Called from the connectThread when a connection has been established.
     * @param socket
     */
    public void onConnected(BluetoothSocket socket){
        socketHandler = new SocketHandler(activity, socket);
        socketHandler.start();
    }

    public class BluetoothNotSupportedException extends Exception{
        public BluetoothNotSupportedException(String message){
            super(message);
        }
    }

    public void connectFailed(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.onDeviceConnectFailed();
            }
        });
    }
}
