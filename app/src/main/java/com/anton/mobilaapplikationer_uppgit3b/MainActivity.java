package com.anton.mobilaapplikationer_uppgit3b;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * TODO: Preference activity
 */
public class MainActivity extends AppCompatActivity {

    private long startTime = 0;
    private PlethDownloader plethDownloader = null;
    private GraphView graphView = null;
    private LineGraphSeries<DataPoint> series;
    private TextView outputLog = null;
    private Button startButton = null;
    private Button uploadButton = null;
    private Button stopButton = null;

    private UploadTask uploadTask;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        graphView = (GraphView)findViewById(R.id.graph);
        outputLog = (TextView)findViewById(R.id.textview_output);
        startButton = (Button)findViewById(R.id.button_start);
        stopButton = (Button)findViewById(R.id.button_cancel);
        uploadButton = (Button)findViewById(R.id.button_upload);

        outputLog.setMovementMethod(new ScrollingMovementMethod());

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        series = new LineGraphSeries();
        graphView.addSeries(series);

        try{
            plethDownloader = new PlethDownloader(this);
        }catch(Exception e){
            Log.d("","This device does not support bluetooth.");
            showToast("This device does not support bluetooth.");
            this.finish();
        }


        if(plethDownloader == null){
            //show some message to the user that bluetooth is not supported.
            outputLog.append("WARNING! Bluetooth is not supported on this device!" + "\n");
            startButton.setEnabled(false);
        }else{

        }
    }

    @Override
    protected void onDestroy() {
        if(plethDownloader!=null)
            plethDownloader.cancel();
        if(uploadTask!=null)
            uploadTask.cancel(true);
        super.onDestroy();
    }

    /**
     * Start button pressed
     */
    public void onButtonStart(View v){
        startTime = System.currentTimeMillis();
        showToast("Starting download...");
        startButton.setEnabled(false);
        plethDownloader.connectToDevice();
    }

    /**
     * Upload pleth data to a server
     * TODO: select file from a list
     * @param v
     */
    public void onButtonUpload(View v){
        uploadButton.setEnabled(false);
        String ip = sharedPreferences.getString("server_ip", null);
        String filename = sharedPreferences.getString("pleth_filename", null);
        int port = Integer.parseInt(sharedPreferences.getString("server_port", null));
        uploadTask = new UploadTask(this, ip, port);
        uploadTask.doInBackground(filename);

    }

    /**
     * Stop button pressed
     */
    public void onButtonStop(View v){
        if(plethDownloader!=null)
            plethDownloader.cancel();
        if(uploadTask!=null)
            uploadTask.cancel(true);
        showToast("Stopping tasks...");

        stopButton.setEnabled(false);
    }

    /**
     * TODO: update gui with pulse rate and pleth.
     * @param data
     */
    public void displayData(SensorData data){
        outputLog.setText("Pulse rate: " + data.getPulseRate());
        outputLog.append("\nElapsed time: " + data.getTime()/1000f);
        series.appendData(new DataPoint(System.currentTimeMillis() - startTime, data.getPleth()), false, 100);
    }

    public void showToast(final CharSequence msg) {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        toast.show();
    }

    public String getFilename(){
        return sharedPreferences.getString("pleth_filename", null);
    }

    public void onButtonSettings(View v){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void onUploadComplete(){
        uploadButton.setEnabled(true);
    }

    public void onDownloadComplete(){
        startButton.setEnabled(true);
        showToast("Download completed.");
    }

    public long getDownloadTime(){
        return Long.parseLong(sharedPreferences.getString("max_measure", null));
    }

    public void onUploadFailed(){
        uploadButton.setEnabled(true);
        showToast("Upload failed.");
    }

    public void onDeviceConnectFailed(){
        startButton.setEnabled(true);
        showToast("Could not connect to device.");
    }

}
