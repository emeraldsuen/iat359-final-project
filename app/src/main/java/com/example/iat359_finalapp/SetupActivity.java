package com.example.iat359_finalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;

public class SetupActivity extends AppCompatActivity {

    TextView destinationNameTextView, destinationNumTextView;
    Place destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        Intent i = getIntent();

        destinationNameTextView = (TextView)findViewById(R.id.locationText);
        String dest_name = i.getStringExtra("DESTINATION_STRING");
        destinationNameTextView.setText(dest_name);

        destinationNumTextView = (TextView)findViewById(R.id.distance_TextView);
        double dist_km = i.getDoubleExtra("DESTINATION_DISTANCE", 0.0);
        destinationNumTextView.setText("Distance: " + dist_km);
    }
}
