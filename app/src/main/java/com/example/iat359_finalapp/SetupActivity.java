package com.example.iat359_finalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;

public class SetupActivity extends AppCompatActivity implements View.OnClickListener {

    TextView destinationNameTextView, destinationNumTextView;
    Place destination;

    Button startButton;

    MyDatabase db;

    String dest_name;
    double dist_km;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        Intent i = getIntent();

        destinationNameTextView = (TextView)findViewById(R.id.locationText);
        dest_name = i.getStringExtra("DESTINATION_STRING");
        destinationNameTextView.setText(dest_name);

        destinationNumTextView = (TextView)findViewById(R.id.distance_TextView);
        dist_km = i.getDoubleExtra("DESTINATION_DISTANCE", 0.0);
        destinationNumTextView.setText("Distance: " + dist_km);


        //database
        db = new MyDatabase(this);

        //start button
        startButton = (Button)findViewById(R.id.startTrip_Button);
        startButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        String name = dest_name;
        String type = "transit";
        dist_km = (int)dist_km;
        String distance = String.valueOf(dist_km) + "km";

        long id = db.insertData(name, type, distance);
        if (id < 0)
        {
            Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
        }

        Intent i = new Intent(SetupActivity.this, InTransit.class);
        startActivity(i);
    }
}
