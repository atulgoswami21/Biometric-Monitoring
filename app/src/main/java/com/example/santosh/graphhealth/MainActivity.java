package com.example.santosh.graphhealth;

import android.content.res.ColorStateList;
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

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private Runnable timerRunnable;
    private Handler timerHandler = new Handler();
    private boolean[] timerFlag = {false};
    private double lastValue = 21d;


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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Patient p = new Patient(1, 11, "Momo", Patient.MALE, 10);

        final GraphView graph = (GraphView) findViewById(R.id.graph);
        Toast.makeText(this, "called in onCreate " , Toast.LENGTH_LONG).show();


        final LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(p.generateData());
        graph.addSeries(series);
        graph.setVisibility(View.GONE);
        graph.setTitle("Health Monitor");

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
                    graph.setVisibility(View.VISIBLE);
                    timerFlag[0] = true;
                    timerRunnable = new Runnable() {
                        @Override
                        public void run() {
                            lastValue += 1d;
                            series.appendData(new DataPoint(lastValue, p.getRandom()),true,40);
                            timerHandler.postDelayed(this, 300);

                        }
                    };
                    timerHandler.postDelayed(timerRunnable, 300);
                }
            }
        });
        stop.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                timerFlag[0] = false;
                timerHandler.removeCallbacks(timerRunnable);
                graph.setVisibility(View.GONE);
            }
        });
    }

    public void openDialogForAdd(View view){
        add_patient addpatient = new add_patient();
        addpatient.show(getFragmentManager(),"Add patient alert");

    }

}
