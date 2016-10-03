package com.example.santosh.graphhealth;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

//Motion Sensor
// SQLITE Database handler and exception

public class MainActivity extends AppCompatActivity implements SensorEventListener{

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
    /**
     * Sensor Members
     */
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;

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
                databaseinit(name.getText().toString(), identity.getText().toString(), age.getText().toString(), sex.getText().toString());
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
                started = 0;
                timerHandler.removeCallbacks(timerRunnable);
                graph.setVisibility(View.GONE);
                AddNewPatient addpatient = new AddNewPatient();
                addpatient.show(getFragmentManager(),"Add patient alert");
            }
        });
    }

    private void databaseinit(String name , String identity , String age, String sex) {
        try{
             SQLiteDatabase dbhandler = openOrCreateDatabase( "patient.db",MODE_PRIVATE, null );
             dbhandler.beginTransaction();
            try{

                dbhandler.execSQL("CREATE TABLE IF NOT EXISTS "
                    + name+"_"+ identity+"_"+ age+"_"+ sex+ " "
                    + "("
                    + " time_stamp double PRIMARY KEY , "
                    + " x_value float, "  // later change to int
                    + " y_value float, "
                    + " z_value float ); " );

                dbhandler.setTransactionSuccessful();
                }
            catch (SQLiteException e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
            finally {
                dbhandler.endTransaction();
            }
        }catch (SQLException e){
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        }

    private void databaseinsert(double timeStamp, float x, float y,float z) {
        try{
            SQLiteDatabase dbhandler = openOrCreateDatabase( "patient.db",MODE_PRIVATE, null );
            dbhandler.beginTransaction();

            final TextView name_current = (TextView) findViewById(R.id.nameTextView);
            final TextView age_current = (TextView) findViewById(R.id.ageTextView);
            final TextView identity_current = (TextView) findViewById(R.id.IdTextView);
            final TextView sex_current = (TextView) findViewById(R.id.sexTextView);

            try{

                dbhandler.execSQL( "insert into "
                                + name_current.getText().toString()+"_"+ identity_current.getText().toString()+"_"+ age_current.getText().toString()+"_"+ sex_current.getText().toString()
                                + " (time_stamp , x_value, y_value, z_value) VALUES ("
                                + "'" + timeStamp +"', '" + x +"', '" + y +"', '" + z +"');"

                );
                dbhandler.setTransactionSuccessful(); //commit your changes.setTransactionSuccessful();
                Toast.makeText(MainActivity.this, "started is  : " + started  +" added" + timeStamp + x + " **** " + y + " **** "+ z + " **** " + "into" +  name_current.getText().toString()+"_"+ identity_current.getText().toString()+"_"+ age_current.getText().toString()+"_"+ sex_current.getText().toString(), Toast.LENGTH_SHORT).show();
            }
            catch (SQLiteException e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
            finally {
                dbhandler.endTransaction();
            }
        }catch (SQLException e){

            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
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

    /**
     * Sensor code block
     */

    protected void sensorInit(){
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //rate is used in this function
        this.sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * This is the event handler for sensor changes
     * @param event : event fed from the android sensor
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor healthMonitorSensor = event.sensor;
        float x, y, z;
        long timeStamp, delta;
        //implementing only for accelerometer
        if(healthMonitorSensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            x = event.values[0];
            y = event.values[1];
            z = event.values[2];

            timeStamp = System.currentTimeMillis();
            delta = timeStamp - lastUpdate;
            if(delta > 100){
                lastUpdate = timeStamp;
                last_x = x;
                last_y = y;
                last_z = z;

                //Toast.makeText(this, "Bitch @: " + x + " : " + y + " : " + z , Toast.LENGTH_LONG).show();
            }
            if (started == 1){
                databaseinsert(timeStamp, x,y,z);
            }


        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * this code block is for unbinding the listeners during application switches
     */
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
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

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            //searchButton = (Button) findViewById(R.id.button1);
            InputStream input = null;
            OutputStream output = null;
            HttpsURLConnection connection = null;
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    // Not implemented
                }

                @Override
                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    // Not implemented
                }
            }};

            try {
                SSLContext sc = SSLContext.getInstance("TLS");

                sc.init(null, trustAllCerts, new java.security.SecureRandom());

                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpsURLConnection) url.openConnection();

                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                //downloadButton.setText(Integer.toString(fileLength));
                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/downloads/" + sUrl[1]);
                //downloadButton.setText("Connecting .....");
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;


        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (result != null) {
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();


            } else {
                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();


                //uninstallApp();
                /*Process install;

	            try {

	            install = Runtime.getRuntime().exec("/system/bin/busybox install " + Environment.getExternalStorageDirectory() + "/downloads/" + "RaRandomFlashlight.apk");

	            int iSuccess = install.waitFor();

	            Log.e("TEST", ""+iSuccess);

	            } catch (IOException e) {
	            	Toast.makeText(context,"I/oException", Toast.LENGTH_SHORT).show();
	            } catch (InterruptedException e) {
	            	Toast.makeText(context,"I/oException", Toast.LENGTH_SHORT).show();
	            }*/
            }
        }
    }

    private class UploadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public UploadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            //searchButton = (Button) findViewById(R.id.button1);
            InputStream input = null;
            OutputStream output = null;
            HttpsURLConnection connection = null;
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    // Not implemented
                }

                @Override
                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    // Not implemented
                }
            }};

            try {
                SSLContext sc = SSLContext.getInstance("TLS");

                sc.init(null, trustAllCerts, new java.security.SecureRandom());

                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpsURLConnection) url.openConnection();

                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                //downloadButton.setText(Integer.toString(fileLength));
                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/downloads/" + sUrl[1]);
                //downloadButton.setText("Connecting .....");
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;


        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (result != null) {
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();


            } else {
                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();


                //uninstallApp();
	            /*Process install;

	            try {

	            install = Runtime.getRuntime().exec("/system/bin/busybox install " + Environment.getExternalStorageDirectory() + "/downloads/" + "RaRandomFlashlight.apk");

	            int iSuccess = install.waitFor();

	            Log.e("TEST", ""+iSuccess);

	            } catch (IOException e) {
	            	Toast.makeText(context,"I/oException", Toast.LENGTH_SHORT).show();
	            } catch (InterruptedException e) {
	            	Toast.makeText(context,"I/oException", Toast.LENGTH_SHORT).show();
	            }*/
            }
        }
    }
}
