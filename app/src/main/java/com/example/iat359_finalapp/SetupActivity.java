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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        Intent i = getIntent();

        dest_lat = i.getDoubleExtra("DESTINATION_LAT", 0.0);
        dest_long = i.getDoubleExtra("DESTINATION_LONG", 0.0);

        destinationNameTextView = (TextView) findViewById(R.id.locationText);
        dest_name = i.getStringExtra("DESTINATION_STRING");
        destinationNameTextView.setText(dest_name);

        distFromDestTextView = (TextView) findViewById(R.id.distanceNum_TextView);
        dist_km = (int) i.getDoubleExtra("DESTINATION_DISTANCE", 0.0);
        distFromDestTextView.setText("1");

        distSeekBar = (SeekBar) findViewById(R.id.distance_seek);
        distSeekBar.setMax(3);
        distSeekBar.setProgress(1);

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

        toneName = (TextView) findViewById(R.id.audio_name);
        toneName.setText("50");

        volSeekBar = (SeekBar) findViewById(R.id.volSeekBar);
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

        vibration = (Switch) findViewById(R.id.vibrate);
        vibration.setChecked(true);

        alarm = (RadioButton) findViewById(R.id.alarm_Radio);
        headphones = (RadioButton) findViewById(R.id.headphones_Radio);


        //database
        db = new MyDatabase(this);

        //start button
        startButton = (Button) findViewById(R.id.startTrip_Button);
        startButton.setOnClickListener(this);


        Intent i3 = getIntent();
        Bundle extras = i3.getExtras();
        if (extras == null) {
            destinationNameTextView.setText(dest_name);

        } else {
            dest_name = i3.getStringExtra("name");
            type = i3.getStringExtra("type");
            dbDistance = i3.getStringExtra("dist");
            dbOutput = i3.getStringExtra("output");
            dbVolume = i3.getStringExtra("vol");
            dbVibrate = i3.getStringExtra("vibrate");
            dbLat = i3.getStringExtra("lat");
            dbLon = i3.getStringExtra("lon");

//            dest_lat = Double.parseDouble(dbLat);
//            dest_long = Double.parseDouble(dbLon);


            Toast.makeText(this, dest_name + ", " + type + ", " + dbDistance + ", " + dbOutput + ", " + dbVolume + ", " + dbVibrate  + ", " + dbLat + ", " + dbLon, Toast.LENGTH_LONG).show();
//            destinationNameTextView.setText(dest_name);
//            distFromDestTextView.setText(dbDistance);
////            Toast.makeText(this, dbDistance, Toast.LENGTH_SHORT).show();
////            distSeekBar.setProgress(Integer.parseInt(dbDistance));
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
//            } else {
//                vibration.setChecked(false);
//            }

        }


    }

    @Override
    public void onClick(View v) {
        //starting trip
        if (v.getId() == R.id.startTrip_Button) {

            String name = dest_name;
            dbType = "transit";
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

            Log.i(TAG, dest_lat + ", " + dest_long);


            //insert data into SQLite db
            long id = db.insertData(name, dbType, dbDistance, dbOutput, dbVolume, dbVibrate, dest_lat, dest_long);
            if (id < 0) {
                Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
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
        }
    }
}
