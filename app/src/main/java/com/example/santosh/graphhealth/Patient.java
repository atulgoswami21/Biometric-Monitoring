package com.example.santosh.graphhealth;

import android.renderscript.Sampler;
import android.util.Log;

import com.jjoe64.graphview.series.DataPoint;

import java.security.Key;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Random;

public class Patient {
    public static boolean MALE = true;
    public static boolean FEMALE = false;
    final String TAG = "Santi";
    public int id;
    public double Age;
    public String Name;
    public Boolean Sex;
    private int LengthOfData;
    private float[] Data;

//    public Dictionary samplePatient = new Dictionary() {
//        @Override
//        public int size() {
//            return 0;
//        }
//
//        @Override
//        public boolean isEmpty() {
//            return false;
//        }
//
//        @Override
//        public Enumeration keys() {
//            return null;
//        }
//
//        @Override
//        public Enumeration elements() {
//            return null;
//        }
//
//        @Override
//        public Object get(Object o) {
//            return null;
//        }
//
//        @Override
//        public Object put(Object o, Object o2) {
//            return null;
//        }
//
//        @Override
//        public Object remove(Object o) {
//            return null;
//        }
//    };
//
//    public Dictionary getSamplePatient() {
//        Log.d(TAG,String.valueOf(samplePatient));
//        return samplePatient;
//    }
//
//    public void initializeDictionary(){
//        samplePatient.put("Name",(String) "");
//        samplePatient.put("Age",(double) 0 );
//        samplePatient.put("Id",(int) 0);
//        samplePatient.put("Sex",(Boolean) true);
//    }


    //complete explicit constructor
    public Patient(int id, double age, String name, Boolean sex, float[] data, int length){
        this.id = id;
        this.Age = age;
        this.Name = name;
        this.Sex = sex;
        this.Data = data;
        this.LengthOfData = length;
    }
    //for case with only length in dataset
    public Patient(int id, double age, String name, Boolean sex, int length){
        this.id = id;
        this.Age = age;
        this.Name = name;
        this.Sex = sex;
        this.LengthOfData = length;
        this.Data = this.genRandomData(length);
    }
    public Patient(int id, double age, String name, Boolean sex){
        this.id = id;
        this.Age = age;
        this.Name = name;
        this.Sex = sex;
        this.Data = this.genRandomData(25);
        this.LengthOfData = 25;

    }
    public float[] getPatientData() {
        return (this.Data);
    }
    public void setPatientData(int len){
        for(int i = 0; i< (len-1);i++){
            this.Data[i] = this.Data[i+1];
        }
        Random keeper = new Random();
        Log.d(TAG, String.valueOf(keeper.nextFloat()));
        this.Data[(len-1)] = keeper.nextFloat();
    }
    private float[] genRandomData(int len){
        float[] results = new float[len];
        Random keeper = new Random();
        for (int i = 0; i < len; i++){
            results[i] = keeper.nextFloat();
        }
        return(results);
    }

//    public void setPatientDetails(int id, double age, String name, Boolean sex){
//        initializeDictionary();
//        samplePatient.put("Name",name);
//        samplePatient.put("Id",0);
//        samplePatient.put("Age",age);
//        samplePatient.put("Sex",sex);
//    }
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

    double mLastRandom = 2;
    Random mRand = new Random();
    public double getRandom() {
        return mLastRandom += mRand.nextDouble()*0.5 - 0.25;
    }
}