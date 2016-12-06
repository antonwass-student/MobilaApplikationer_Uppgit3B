package com.anton.mobilaapplikationer_uppgit3b;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Anton on 2016-12-06.
 */

public class PlethDownloader {

    private final static int REQUEST_ENABLE_BT = 1;
    private MainActivity activity;
    private Spinner pairedDevicesList;
    private BluetoothDevice selectedDevice;

    private ConnectThread connectThread;
    private SocketHandler socketHandler;

    private LinkedList<PlethData> plethData = new LinkedList();

    public PlethDownloader(MainActivity activity, Spinner pairedDevicesList) throws BluetoothNotSupportedException{
        this.activity = activity;
        this.pairedDevicesList = pairedDevicesList;

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

        ArrayAdapter<BTWrap> arrayAdapter =
                new ArrayAdapter(
                        activity.getApplicationContext(),
                        R.layout.support_simple_spinner_dropdown_item
                );

        for(BluetoothDevice device : pairedDevices){
            arrayAdapter.add(new BTWrap(device));
        }

        pairedDevicesList.setAdapter(arrayAdapter);

        pairedDevicesList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDevice = ((BTWrap)parent.getSelectedItem()).getDevice();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedDevice = null;
            }
        });

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
     *
     * @param data
     */
    public synchronized void addPlethdata(PlethData data){
        plethData.addLast(data);
    }

    public synchronized PlethData[] takePlethData(){
        PlethData[] data = plethData.toArray(new PlethData[]{});
        plethData.clear();
        return data;
    }

    /**
     * closes all threads.
     */
    public void cancel(){
        connectThread.cancel();
        socketHandler.cancel();
    }

    /**
     * Called from the connectThread when a connection has been established.
     * @param socket
     */
    public void onConnected(BluetoothSocket socket){
        socketHandler = new SocketHandler(this, socket);
        socketHandler.start();
    }

    public class BluetoothNotSupportedException extends Exception{
        public BluetoothNotSupportedException(String message){
            super(message);
        }
    }
}
