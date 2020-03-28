package com.example.iat359_finalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button startButton, historyButton, settingsButton;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("TEST", "MainActivity");

        //start buton initalize
        startButton = (Button) findViewById(R.id.start_button);
        startButton.setOnClickListener(this);

        //history button initialize
        historyButton = (Button) findViewById(R.id.history_button);
        historyButton.setOnClickListener(this);

        //settings button initialize
        settingsButton = (Button) findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(this);

        //getting current location
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //check if gps is enabled
        gpsStatus();


    }

    @Override
    public void onClick(View v) {
        //if start button is pressed
        if (v.getId() == R.id.start_button) {
            Intent i = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(i);
        }
        if (v.getId() == R.id.history_button) {
            Intent i = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(i);
        }
        if (v.getId() == R.id.settings_button) {
            Intent i = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(i);
        }
    }

    public void gpsStatus() {
        //check if gps is enabled
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("GPS is disabled. Enable it now?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }
    }
}
