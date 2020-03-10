package com.example.iat359_finalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    Button startButton, historyButton, settingsButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //start buton initalize
        startButton = (Button)findViewById(R.id.start_button);
        startButton.setOnClickListener(this);

        //history button initialize
        historyButton = (Button)findViewById(R.id.history_button);
        historyButton.setOnClickListener(this);

        //settings button initialize
        settingsButton = (Button)findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(this);




    }

    @Override
    public void onClick(View v) {
        //if start button is pressed
        if(v.getId() == R.id.start_button){
            Intent i = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(i);
        }
        if(v.getId() == R.id.history_button){
            Intent i = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(i);
        }
        if(v.getId() == R.id.settings_button){
            Intent i = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(i);
        }
    }
}
