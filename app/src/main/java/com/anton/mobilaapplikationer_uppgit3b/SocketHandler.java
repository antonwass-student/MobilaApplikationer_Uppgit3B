package com.anton.mobilaapplikationer_uppgit3b;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.renderscript.ScriptGroup;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Anton on 2016-12-06.
 */


/**
 * Reads data from the bluetooth device and saves it to a file on the phone.
 * Also sends received data to be displayed to GUI.
 */
public class SocketHandler extends Thread {

    private static final byte[] FORMAT_2 = {0x02,0x70,0x04,0x02,0x02,0x00,0x78,0x03};
    public static final int ACK = 0x06;
    public static final int NAK = 0x15;
    public static final int PACKET_SIZE = 125;

    private BluetoothSocket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private boolean reading;
    private MainActivity activity;
    String filename = "";

    public SocketHandler(MainActivity activity, BluetoothSocket socket){
        this.socket = socket;
        this.activity = activity;

        try {
            this.inputStream = socket.getInputStream();
            this.outputStream = socket.getOutputStream();

            this.outputStream.write(FORMAT_2);
            this.outputStream.flush();

            int response = inputStream.read();

            if(response == ACK){
                //format is accepted
                readData();
            }else if(response == NAK){
                //format is not accepted

            }else{
                //??????
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void cancel(){
        reading = false;
    }

    /**
     * Continously read data from bluetooth device.
     */
    public void readData(){
        long startTime = System.currentTimeMillis();
        long downloadTime = activity.getDownloadTime();

        FileOutputStream outputStream = null;
        PrintWriter pw = null;
        reading = true;

        try {
            outputStream = activity.openFileOutput(activity.getFilename(), Context.MODE_PRIVATE);
            pw = new PrintWriter(outputStream);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            pw.println(sdf.format(Calendar.getInstance().getTime()));

            while(reading && System.currentTimeMillis() - startTime < downloadTime){
                byte[] buffer = new byte[PACKET_SIZE];
                int readBytes = 0;
                while(readBytes < PACKET_SIZE){
                    readBytes += inputStream.read(buffer, readBytes, PACKET_SIZE - readBytes);
                }

                SensorData sd = extractData(buffer);

                sd.setTime(System.currentTimeMillis() - startTime);

                sendToUI(sd);

                pw.println(sd.getPleth());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            pw.close();
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            downloadComplete();
        }
    }

    /**
     * Extracts data from packet.
     * TODO: get pleth data
     * @param data
     */
    public SensorData extractData(byte[] data){
        //read byte 3 and 8. put them together to form an integer.
        int msb = data[3]; // - 6 5 4 3 2 1 0
        int lsb = data[8];// - - - - - - 8 7

        msb = msb << 7;

        // 0 0 6 5 4 3 2 1 0
        // +
        // 8 7 0 0 0 0 0 0 0
        // =
        // 8 7 6 5 4 3 2 1 0

        int pulse = msb + lsb;

        int pleth = unsignedByteToInt(data[2]);

        return new SensorData(pleth, pulse);
    }

    private int unsignedByteToInt(byte b) {
        return (int) b & 0xFF;
    }

    private void sendToUI(final SensorData data){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.displayData(data);
            }
        });
    }

    private void downloadComplete(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.onDownloadComplete();
            }
        });
    }
}
