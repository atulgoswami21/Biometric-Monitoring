package com.example.santosh.graphhealth;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        float[] sample = new float[]{0, 0, 0, 0, 0};
        String[] hlabel = new String[]{"X", "1","2","3"};
        String[] vlabel = new String[]{"Y", "10","20","30"};
        final boolean[] timerFlag = {false};
        final String MY_TAG = "debugging";

        final Patient p = new Patient(1, 11, "Momo", Patient.MALE, 10);
        final GraphView graph = new GraphView(this, sample, "Health Graph", hlabel, vlabel, GraphView.LINE);

        graph.setBackgroundColor(Color.BLACK);

        final Handler timerHandler = new Handler();
        final Runnable timerRunnable = new Runnable() {
            @Override
            public void run() {
                timerFlag[0] = true;
                p.setPatientData(10);
                float f[] = p.getPatientData();
                Log.d(MY_TAG, "Chal chutiye");
                graph.setValues(f);
                //Redraw the graph
                graph.invalidate();
                // Repost the run method in the queue so that it can be called again after 100 ms
                timerHandler.postDelayed(this, 500);
            }
        };


        Button start = (Button) findViewById(R.id.startButton);
        Button stop = (Button) findViewById(R.id.stopButton);
        Button add = (Button) findViewById(R.id.addButton);
        TextView name = (TextView) findViewById(R.id.nameTextView);
        TextView age = (TextView) findViewById(R.id.ageTextView);
        TextView identity = (TextView) findViewById(R.id.IdTextView);
        TextView sex = (TextView) findViewById(R.id.sexTextView);

        name.setText(p.Name);
        age.setText(String.valueOf(p.Age));
        identity.setText(String.valueOf(p.id));
        sex.setText(String.valueOf(p.Sex));


        start.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                if (!timerFlag[0]) {
                    timerHandler.postDelayed(timerRunnable, 0);
                }
            }
        });
        stop.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                timerFlag[0] = false;
                timerHandler.removeCallbacks(timerRunnable);
                float f1[] = {0,0};
                graph.setValues(f1);
//                 Redraw the graph
                graph.invalidate();

            }
        });

        LinearLayout layout = (LinearLayout) findViewById(R.id.layoutForGraph);
        layout.addView(graph);

        // Spinner element
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Santi");
        categories.add("Momo");
        categories.add("Mamu");
        categories.add("Shukla");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
        final Patient p = new Patient(2, 22, item, Patient.MALE, 20);
        TextView name = (TextView) findViewById(R.id.nameTextView);
        name.setText(item);

        float f1[] = {0,0};
        //graph.setValues(f1);
//      Redraw the graph
        //graph.invalidate();
        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

}
