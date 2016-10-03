package com.example.santosh.graphhealth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;

//Motion Sensor
// SQLITE Database handler and exception

public class MainActivity extends AppCompatActivity{

    ProgressDialog mProgressDialog;
    String dbName;
    double mLastRandom = 2;
    Random mRand = new Random();
    private Runnable timerRunnable;
    private Handler timerHandler = new Handler();
    //private boolean[] timerFlag = {false};
    private double lastValue = 21d;
    private String PatientName = "Patient";

    private int started;

    //name, age, id, sex
    public static void setPatientText(String Name, String Age, String ID, String SEX,View view){

        TextView name = (TextView) view.findViewById(R.id.nameTextView);
        TextView age = (TextView) view.findViewById(R.id.ageTextView);
        TextView identity = (TextView) view.findViewById(R.id.IdTextView);
        TextView sex = (TextView) view.findViewById(R.id.sexTextView);

        name.setText(Name);
        age.setText(String.valueOf(Age));
        identity.setText(String.valueOf(ID));
        sex.setText(String.valueOf(SEX));
    }
    //insert into table

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize sensors
        sensorInit();

        final GraphView graph = (GraphView) findViewById(R.id.graph);
//        Toast.makeText(this, "called in onCreate " , Toast.LENGTH_LONG).show();
//

        final LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(generateData());
        graph.addSeries(series);
        graph.setVisibility(View.GONE);


        Button start = (Button) findViewById(R.id.startButton);
        Button stop = (Button) findViewById(R.id.stopButton);
        Button add = (Button) findViewById(R.id.addButton);
        Button downloadButton = (Button) findViewById(R.id.downloadButton);
        downloadButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                processDownloadClick();
            }
        });
        Button uploadButton = (Button) findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                processUploadClick();
            }
        });


        mProgressDialog = new ProgressDialog(MainActivity.this);
        //mProgressDialog.setMessage("A message");
        //mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        //mProgressDialog.setCancelable(true);

        final TextView name = (TextView) findViewById(R.id.nameTextView);
        final TextView age = (TextView) findViewById(R.id.ageTextView);
        final TextView identity = (TextView) findViewById(R.id.IdTextView);
        final TextView sex = (TextView) findViewById(R.id.sexTextView);

        name.setText("Patient");
        age.setText(String.valueOf(22));
        identity.setText(String.valueOf(110));
        sex.setText(String.valueOf("M"));


        start.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){


                started = 1;

                graph.setVisibility(View.VISIBLE);
                graph.setTitle("Health Graph for "+name.getText().toString());
                    timerHandler.removeCallbacks(timerRunnable);
                    timerRunnable = new Runnable() {
                        @Override
                        public void run() {
                            lastValue += 1d;
                            series.appendData(new DataPoint(lastValue, getRandom()),true,40);
                            timerHandler.postDelayed(this, 300);

                        }
                    };
                    timerHandler.postDelayed(timerRunnable, 300);
            }


        });
        stop.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                started = 0;
                timerHandler.removeCallbacks(timerRunnable);
                graph.setVisibility(View.GONE);
            }
        });
        add.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //start service
                startService(new Intent(MainActivity.this, Accelerometer.class));

                started = 0;
                timerHandler.removeCallbacks(timerRunnable);
                graph.setVisibility(View.GONE);
                AddNewPatient addpatient = new AddNewPatient();
                addpatient.show(getFragmentManager(),"Add patient alert");
            }
        });
    }



    public DataPoint[] generateData() {
        int count = 20;
        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count; i++) {
            double x = i;
            double f = mRand.nextDouble()*0.15+0.3;
            double y = Math.sin(i*f+2) + mRand.nextDouble()*0.3;
            DataPoint v = new DataPoint(x, y);
            values[i] = v;
        }
        return values;
    }

    public double getRandom() {
        return mLastRandom += mRand.nextDouble()*0.5 - 0.25;
    }

    private void processDownloadClick() {

        final DownloadTask downloadTask = new DownloadTask(MainActivity.this);
        downloadTask.execute("https://impact.asu.edu/" + dbName, dbName);

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                downloadTask.cancel(true);
            }
        });
    }

    private void processUploadClick() {

        final UploadTask UploadTask = new UploadTask(MainActivity.this);
        UploadTask.execute("https://impact.asu.edu/" + dbName, dbName);

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                UploadTask.cancel(true);
            }
        });
    }
}
