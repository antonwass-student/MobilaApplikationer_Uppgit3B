package com.anton.mobilaapplikationer_uppgit3b;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Anton on 2016-12-06.
 */

public class UploadTask extends AsyncTask<String, Integer, Void> {

    private MainActivity activity;
    private String ip;
    private int port;

    public UploadTask(MainActivity activity, String ip, int port) {
        this.activity = activity;
        this.ip = ip;
        this.port = port;
    }

    @Override
    protected Void doInBackground(String... params) {

        FileInputStream fileInputStream;
        InputStreamReader inputStreamReader;
        BufferedReader bufferedReader = null;
        Socket socket = null;
        PrintWriter pw = null;

        try {
            String filename = params[0];
            fileInputStream = activity.openFileInput(filename);
            inputStreamReader = new InputStreamReader(fileInputStream);
            bufferedReader = new BufferedReader(inputStreamReader);

            socket = new Socket(ip, port);

            pw = new PrintWriter(socket.getOutputStream());

            String line;

            while((line = bufferedReader.readLine()) != null){
                if(isCancelled()){
                    break;
                }
                pw.println(line);
            }

            pw.flush();

        } catch (IOException e) {
            e.printStackTrace();
            onUploadError();
        }finally {

            try {
                if(bufferedReader!=null)
                    bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(pw!=null)
                pw.close();

            try {
                if(socket!=null)
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        activity.showToast("File upload cancelled.");
        activity.onUploadComplete();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        activity.showToast("File upload complete.");
        activity.onUploadComplete();
    }

    private void onUploadError(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.onUploadFailed();
            }
        });
    }

}
