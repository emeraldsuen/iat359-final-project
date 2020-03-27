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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        Log.i("TEST", "SetupActivity");

        Intent i = getIntent();

        dest_lat = i.getDoubleExtra("DESTINATION_LAT", 0.0);
        dest_long = i.getDoubleExtra("DESTINATION_LONG", 0.0);

        destinationNameTextView = (TextView) findViewById(R.id.locationText);
        dest_name = i.getStringExtra("DESTINATION_STRING");
        destinationNameTextView.setText(dest_name);

        distFromDestTextView = (TextView) findViewById(R.id.distanceNum_TextView);
        dist_km = (int) i.getDoubleExtra("DESTINATION_DISTANCE", 0.0);
//        distFromDestTextView.setText("Distance: " + dist_km);
        distFromDestTextView.setText("");


        distSeekBar = (SeekBar) findViewById(R.id.distance_seek);
//        distSeekBar.setMax(dist_km);
        distSeekBar.setMax(3);
        distSeekBar.setProgress(dist_km / 2);

        volSeekBar = (SeekBar) findViewById(R.id.volSeekBar);
        volSeekBar.setMax(100);
        volSeekBar.setProgress(50);

        vibration = (Switch) findViewById(R.id.vibrate);
        vibration.setChecked(true);

        alarm = (RadioButton) findViewById(R.id.alarm_Radio);
        headphones = (RadioButton) findViewById(R.id.headphones_Radio);

        toneName = (TextView) findViewById(R.id.audio_name);

        //database
        db = new MyDatabase(this);

        //start button
        startButton = (Button) findViewById(R.id.startTrip_Button);
        startButton.setOnClickListener(this);

        //SeekBarListener
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


    }

    @Override
    public void onClick(View v) {
        //starting trip
        if (v.getId() == R.id.startTrip_Button) {
            if (vibration.isChecked()) {
                vibrate = true;
            } else {
                vibrate = false;
            }

            if (alarm.isChecked()) {
                outputType = "alarm";
            } else if (headphones.isChecked()) {
                outputType = "headphones";
            } else {
                Toast.makeText(this, "Please select a output source.", Toast.LENGTH_SHORT).show();
            }

            String name = dest_name;
            String type = "transit";
            String distance = progressChangedValue + "km";
            String volume = "" + volChangedValue;

            long id = db.insertData(name, type, distance, volume);
            if (id < 0) {
                Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
            }

            double sigh = distSeekBar.getProgress();
            inTransit = true;

            Intent i = new Intent(SetupActivity.this, MapsActivity.class);

            //attach extras
            i.putExtra("InTransit", inTransit);
            i.putExtra("Dest_lat", dest_lat);
            i.putExtra("Dest_long", dest_long);
            i.putExtra("Dest_name", name);
            i.putExtra("Dest_dist", sigh);
            i.putExtra("OUTPUT", outputType);
            i.putExtra("VOL", volChangedValue);
            i.putExtra("VIBRATE", vibrate);

            startActivity(i);
        }
    }
}
