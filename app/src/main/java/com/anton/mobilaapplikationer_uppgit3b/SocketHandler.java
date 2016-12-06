package com.anton.mobilaapplikationer_uppgit3b;

import android.bluetooth.BluetoothSocket;
import android.renderscript.ScriptGroup;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Anton on 2016-12-06.
 */

public class SocketHandler extends Thread {

    private static final byte[] FORMAT_2 = {0x02,0x70,0x04,0x02,0x02,0x00,0x78,0x03};

    private PlethDownloader plethDownloader;
    private BluetoothSocket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private boolean reading;

    public SocketHandler(PlethDownloader pd, BluetoothSocket socket){
        this.plethDownloader = pd;
        this.socket = socket;

        try {
            this.inputStream = socket.getInputStream();
            this.outputStream = socket.getOutputStream();


            outputStream.write(FORMAT_2);
            if(inputStream.read() == 0x06){
                //format is accepted
                readData();
            }else{
                //format is not accepted
                closeConnection();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cancel(){
        reading = false;
    }

    private void closeConnection(){
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readData(){
        byte[] buffer = new byte[125];
        reading = true;

        try {
            while(reading){
                while(inputStream.read(buffer) < 125){}

                extractData(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            closeConnection();
        }
    }

    public void extractData(byte[] data){
        //read byte 97 and 102. put them together to form an integer.
        int msb = data[97]; // - 6 5 4 3 2 1 0
        int lsb = data[102];// - - - - - - 8 7

        msb = msb << 7;

        // 0 0 6 5 4 3 2 1 0
        // +
        // 8 7 0 0 0 0 0 0 0
        // =
        // 8 7 6 5 4 3 2 1 0

        int pulse = msb + lsb;

        plethDownloader.addPlethdata(new PlethData(System.currentTimeMillis(), pulse));
    }
}
