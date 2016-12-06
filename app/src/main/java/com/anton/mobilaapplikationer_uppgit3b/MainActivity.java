package com.anton.mobilaapplikationer_uppgit3b;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    PlethDownloader pd = null;
    GraphView graphView = null;
    LineGraphSeries<DataPoint> series;
    TextView outputLog = null;
    ScrollView scrollView = null;

    UploadThread uploader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner pairedDevicesList = (Spinner) findViewById(R.id.paired_devices);
        graphView = (GraphView)findViewById(R.id.graph);
        outputLog = (TextView)findViewById(R.id.textview_output);
        scrollView = (ScrollView)findViewById(R.id.output_scroll);

        outputLog.setMovementMethod(new ScrollingMovementMethod());

        series = new LineGraphSeries();
        graphView.addSeries(series);

        try{
            pd = new PlethDownloader(this, pairedDevicesList);
        }catch(Exception e){
            Log.d("","This device does not support bluetooth.");
        }


        if(pd == null){
            //show some message to the user that bluetooth is not supported.
        }else{
            uploader = new UploadThread(pd, "", 1234);
            uploader.start();
        }
    }

    @Override
    protected void onDestroy() {
        uploader.cancel();

        super.onDestroy();
    }

    /**
     * Start button pressed
     */
    public void onButtonStart(View v){
        outputLog.append("Starting..."+"\n");
    }

    /**
     * Stop button pressed
     */
    public void onButtonStop(View v){
        outputLog.append("Stopping..."+"\n");
    }
}
