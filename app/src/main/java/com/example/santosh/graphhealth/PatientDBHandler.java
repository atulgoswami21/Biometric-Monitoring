package com.example.santosh.graphhealth;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;


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

import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;
import android.widget.Toast;

/**
 * Created by Mohd on 10/2/2016.
 */


public class PatientDBHandler extends AppCompatActivity {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ProductGroup29DB.db";
    //public static final String TABLE_PRODUCTS = "products";
    //public static final String COLUMN_ID = "_id";
    //public static final String COLUMN_PRODUCTNAME = "productname";




    public void onCreateDB( String table_name ) {

        try{
            SQLiteDatabase dbhandler = openOrCreateDatabase( DATABASE_NAME,MODE_PRIVATE, null );
            dbhandler.beginTransaction();
            try{

                dbhandler.execSQL("CREATE TABLE IF NOT EXISTS "
                        + table_name + " "
                        + "("
                        + " time_stamp double PRIMARY KEY , "
                        + " x_value float, "  // later change to int
                        + " y_value float, "
                        + " z_value float ); " );

                dbhandler.setTransactionSuccessful();
            }
            catch (SQLiteException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
            finally {
                dbhandler.endTransaction();
            }
        }catch (SQLException e){
            Toast.makeText( this , e.getMessage(), Toast.LENGTH_LONG).show();
        }

        }

    private void OnInsertDB(String table_name,double timeStamp, float x, float y,float z) {
        try{
            SQLiteDatabase dbhandler = openOrCreateDatabase( DATABASE_NAME,MODE_PRIVATE, null );
            dbhandler.beginTransaction();



            try{

                dbhandler.execSQL( "insert into "
                        + table_name
                        + " (time_stamp , x_value, y_value, z_value) VALUES ("
                        + "'" + timeStamp +"', '" + x +"', '" + y +"', '" + z +"');"

                );
                dbhandler.setTransactionSuccessful(); //commit your changes.setTransactionSuccessful();

            }
            catch (SQLiteException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
            finally {
                dbhandler.endTransaction();
            }
        }catch (SQLException e){

            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    }




