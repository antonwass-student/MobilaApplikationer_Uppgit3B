package com.anton.mobilaapplikationer_uppgit3b;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Anton on 2016-12-06.
 */

public class UploadThread extends Thread {

    private PlethDownloader plethDownloader;
    private String ip;
    private int port;
    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;


    private boolean uploading;
    private final long UPLOAD_DELAY = 1000;

    public UploadThread(PlethDownloader pd, String ip, int port){
        //connect
        this.plethDownloader = pd;
        this.ip = ip;
        this.port = port;
    }


    @Override
    public void run(){
        try {
            socket = new Socket(ip, port);

            inputStream = socket.getInputStream();

            outputStream = socket.getOutputStream();

            startUpload();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cancel(){
        uploading = false;

        interrupt();


        try{
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try{
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try{
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startUpload(){
        uploading = true;
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(uploading){
            //get data
            PlethData[] data = plethDownloader.takePlethData();

            if(data.length > 0){
                //upload
                for(PlethData pd : data){
                    try {
                        oos.writeObject(pd);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                sleep(UPLOAD_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
