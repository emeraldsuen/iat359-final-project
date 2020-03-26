package com.example.iat359_finalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DecimalFormat;

public class InTransit extends AppCompatActivity {

    TextView currLatTextView, currLongTextView, destLatTextView, destLongTextView, distFromDest;
    Double currLat, currLong, destLat, destLong, dist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_transit);

        Intent i = getIntent();
        currLatTextView = (TextView) findViewById(R.id.currLatTextView);
        currLongTextView = (TextView) findViewById(R.id.currLongTextView);
        destLatTextView = (TextView) findViewById(R.id.destLatTextView);
        destLongTextView = (TextView) findViewById(R.id.destLongTextView);
        distFromDest = (TextView) findViewById(R.id.distFromDest);

        currLat = i.getDoubleExtra("CURRLAT", 0.0);
        currLong = i.getDoubleExtra("CURRLONG", 0.0);
        destLat = i.getDoubleExtra("DESTLAT", 0.0);
        destLong = i.getDoubleExtra("DESTLONG", 0.0);

        currLatTextView.setText("" + currLat);
        currLongTextView.setText("" + currLong);
        destLatTextView.setText("" + destLat);
        destLongTextView.setText("" + destLong);

        dist = CalculationByDistance(currLat, currLong, destLat, destLong);

        distFromDest.setText("" + dist);
    }

    public double CalculationByDistance(double lat1, double long1, double lat2, double long2) {
        int Radius = 6371;// radius of earth in Km

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(long2 - long1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }
}
