package com.example.santosh.graphhealth;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.List;

//import static android.app.PendingIntent.getActivity;

/**
 * Created by Mohd on 9/6/2016.
 */
public class add_patient extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        //new alertdialog for displaying add_patient.xml
        AlertDialog.Builder addpbuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater addpinflator = getActivity().getLayoutInflater();
        View view =addpinflator.inflate(R.layout.addpatient,null);
        addpbuilder.setView(view);

        //no action on negative button press
        addpbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        addpbuilder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                Dialog f = (Dialog) dialog;
            //Declaring Edit text and radiogroups
                final EditText patientnameET = (EditText) f.findViewById(R.id.pname);
                final EditText patientageET =(EditText) f.findViewById(R.id.age);
                final EditText patientidET =(EditText) f.findViewById(R.id.pid);
                RadioGroup patientsexRG = (RadioGroup) f.findViewById(R.id.sex);

                //Determining GENDER selected
                String  patientsex;
                int selected = patientsexRG.getCheckedRadioButtonId();
                RadioButton radioselected = (RadioButton) f.findViewById(selected);

                if (radioselected.getText() == "Male")
                { patientsex= "Male";}
                else
                {patientsex = "Female";}


                //set on focuschange listner
//                patientageET.setFocusable(true);
//                patientnameET.setFocusable(true);
//                patientidET.setFocusable(true);

                //user input validation
//
//                patientageET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                    @Override
//                    public void onFocusChange(View view, boolean b) {
//                        if(b){
//                            if(patientageET.getText().toString().isEmpty())
//                            {
//                                patientageET.setError("Patient age Should not be blank");
//                            }
//                        }
//                    }
//                });
//                patientnameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                    @Override
//                    public void onFocusChange(View view, boolean b) {
//                        if(!b){
//                            if(patientnameET.getText().toString().isEmpty())
//                            {
//                                patientnameET.setError("Patient name Should not be blank");
//                            }
//                        }
//                    }
//                });
//                patientidET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                    @Override
//                    public void onFocusChange(View view, boolean b) {
//                        if(!b){
//                            if(patientidET.getText().toString().isEmpty())
//                            {
//                                patientidET.setError("Patient ID Should not be blank");
//                            }
//                        }
//                    }
//                });
                //Fetching the user data
                if (!patientageET.getText().toString().isEmpty() && !patientnameET.getText().toString().isEmpty() && !patientidET.getText().toString().isEmpty()  ) {
                    String patientage = patientageET.getText().toString();
                    String patientname = patientnameET.getText().toString();
                    String patientid = patientidET.getText().toString();
                    //new Patient

                    MainActivity.setPatientText(patientname,patientage,patientid, patientsex,getActivity().findViewById(R.id.upperLayoutForButtons));
                }

            }
        });


        Dialog dialog=addpbuilder.create();
        return dialog;
    }
}
