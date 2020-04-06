package com.example.iat359_finalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;

import org.w3c.dom.Text;

import java.io.File;

public class SetupActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "TEST";
    TextView destinationNameTextView;
    TextView distFromDestTextView;
    TextView toneName;
    SeekBar distSeekBar, volSeekBar;
    RadioButton alarm, headphones;
    Place destination;
    Switch vibration;
    Boolean vibrate;

    boolean inTransit = false;
    Button startButton;

    MyDatabase db;

    String dest_name;
    int dist_km;

    String outputType;
    int progressChangedValue = 0;
    int volChangedValue;
    String s;
    double dest_lat, dest_long;
    String type;

    String dbName, dbType, dbDistance, dbOutput, dbVolume, dbVibrate, dbLat, dbLon;
    int distParse = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        Intent i = getIntent();

        dest_lat = i.getDoubleExtra("DESTINATION_LAT", 0.0);
        dest_long = i.getDoubleExtra("DESTINATION_LONG", 0.0);

        destinationNameTextView = (TextView) findViewById(R.id.locationText);
        dest_name = i.getStringExtra("DESTINATION_STRING");

        distFromDestTextView = (TextView) findViewById(R.id.distanceNum_TextView);
        dist_km = (int) i.getDoubleExtra("DESTINATION_DISTANCE", 0.0);

        distSeekBar = (SeekBar) findViewById(R.id.distance_seek);

        volSeekBar = (SeekBar) findViewById(R.id.volSeekBar);

        vibration = (Switch) findViewById(R.id.vibrate);

        alarm = (RadioButton) findViewById(R.id.alarm_Radio);
        headphones = (RadioButton) findViewById(R.id.headphones_Radio);

        toneName = (TextView) findViewById(R.id.audio_name);

        setupForm();

        //get extras from database (myadapter)
        Intent i3 = getIntent();
        Bundle extras = i3.getExtras();
        if (extras == null) {
            destinationNameTextView.setText(dest_name);
        } else {
//            Toast.makeText(this, "SUCCESS!!!", Toast.LENGTH_SHORT).show();
            dbName = i3.getStringExtra("name");
            type = i3.getStringExtra("type");
            dbDistance = i3.getStringExtra("dist");
            dbOutput = i3.getStringExtra("output");
            dbVolume = i3.getStringExtra("vol");
            dbVibrate = i3.getStringExtra("vibrate");
            dbLat = i3.getStringExtra("lat");
            dbLon = i3.getStringExtra("lon");

            if (dbLat != null && dbLon != null) {
                dest_lat = Double.parseDouble(dbLat);
                dest_long = Double.parseDouble(dbLon);
            }

            if (dbName != null) {
                dest_name = dbName;
            }

            destinationNameTextView.setText(dest_name);

            //other database junk that wasnt' used
//            distFromDestTextView.setText(dbDistance);
//            if (dbDistance != null) {
//                Log.i(TAG, dbDistance);
////                distParse = Integer.parseInt(dbDistance);
//            }
//            distSeekBar.setProgress(distParse);
//            if (dbOutput == "headphones") {
//                headphones.setChecked(true);
//                alarm.setChecked(false);
//            } else {
//                headphones.setChecked(false);
//                alarm.setChecked(true);
//            }
//            toneName.setText(dbVolume);
////            int volParse = Integer.parseInt(dbVolume);
////            Toast.makeText(this, volParse, Toast.LENGTH_SHORT).show();
////            volSeekBar.setProgress(Integer.parseInt(dbVolume));
//            if (dbVibrate == "true") {
//                vibration.setChecked(true);
//                Toast.makeText(this, "success changed true", Toast.LENGTH_SHORT).show();
//            } else {
//                vibration.setChecked(false);
//            }

        }


    }

    public void setupForm() {
        //database
        db = new MyDatabase(this);

        //destination name
        destinationNameTextView.setText(dest_name);

        //distance textview and seekbar
        distFromDestTextView.setText("");
        distSeekBar.setMax(3);
        distSeekBar.setProgress(dist_km / 2);

        //SeekBarListener for distance
        distSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                distFromDestTextView.setText("" + progressChangedValue);
            }
        });

        //volume stuff
        volSeekBar.setMax(100);
        volSeekBar.setProgress(50);
        //seekbar for volume
        volSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                volChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                toneName.setText("" + volChangedValue);
            }
        });

        vibration.setChecked(true);

        //start button
        startButton = (Button) findViewById(R.id.startTrip_Button);
        startButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        //starting trip
        if (v.getId() == R.id.startTrip_Button) {

            String name = dest_name;
            dbType = "transit";
            if (distFromDestTextView.getText() == null || progressChangedValue == 0) {
                Toast.makeText(this, "Please select a distance value that is greater than 0.", Toast.LENGTH_SHORT).show();
            } else {

            }

            dbDistance = progressChangedValue + "km";

            dbVolume = "" + volChangedValue;

            if (vibration.isChecked()) {
                vibrate = true;
                dbVibrate = "true";
            } else {
                vibrate = false;
                dbVibrate = "false";
            }

            if (alarm.isChecked()) {
                outputType = "alarm";
                dbOutput = "alarm";
            } else if (headphones.isChecked()) {
                outputType = "headphones";
                dbOutput = "headphones";
            } else {
                Toast.makeText(this, "Please select a output source.", Toast.LENGTH_SHORT).show();
                dbOutput = null;
            }

            String latString = Double.toString(dest_lat);
            String lonString = Double.toString(dest_long);
            Log.i(TAG, latString + ", " + lonString);

            if (distFromDestTextView.getText() != null && progressChangedValue != 0 && volChangedValue != 0 && toneName.getText() != null) {

                //insert data into SQLite db
                if (name != null) {
                    long id = db.insertData(name, dbType, dbDistance, dbOutput, dbVolume, dbVibrate, latString, lonString);
                    if (id < 0) {
                        Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
                    }
                }

                double sigh = distSeekBar.getProgress();
                inTransit = true;

                //directs to mapsactivity with trip started
                Intent i = new Intent(SetupActivity.this, MapsActivity.class);

                //attach extras
                i.putExtra("InTransit", inTransit);
                i.putExtra("Dest_lat", dest_lat);
                i.putExtra("Dest_long", dest_long);
                i.putExtra("Dest_name", dbName);
                i.putExtra("Dest_dist", sigh);
                i.putExtra("OUTPUT", outputType);
                i.putExtra("VOL", volChangedValue);
                i.putExtra("VIBRATE", vibrate);

                startActivity(i);
            } else {
                Toast.makeText(this, "Please select all fields.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
