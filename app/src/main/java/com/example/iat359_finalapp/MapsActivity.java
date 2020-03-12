package com.example.iat359_finalapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.security.acl.Permission;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    private GoogleMap gMap;
    private MarkerOptions i_place, f_place, curr_place;
    private Polyline currentPolyline;
    Button getDirButton;
    LocationManager locationManager;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //button
        getDirButton = (Button)findViewById(R.id.getDirection_button);
        getDirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get route
            }
        });

        //setting up the places
        i_place = new MarkerOptions().position(new LatLng(49.282730, -123.120735)).title("Location 1");
        f_place = new MarkerOptions().position(new LatLng(49.248810, -122.980507)).title("Location 2");



        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(MapsActivity.this);

        String url = getUrl(i_place.getPosition(), f_place.getPosition(), "driving");
        new FetchURL(MapsActivity.this).execute(url,"driving");

        //getting current location
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        //check if gps is enabled
        gpsStatus();
        
        //ask permission
//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(
//                    this,
//                    new String[] {Manifest.permission.ACCESS_COARSE_LOCATION},
//                    MY_PERMISSIONS_ACCESS_COURSE_LOCATION
//            );
//        }

        //You have to ask user for permission for location services
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(gMap!=null){
                gMap.setMyLocationEnabled(true);
            }
        }
        else{
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION, true);
        }



    }


    //not sure if this is working but this is from another gps tutorial
    //it's where it constantly check location changing
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Toast.makeText(getBaseContext(), "Location changed : Lat: " + location.getLatitude() + " Lng: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
            String latitude = "Latitude: " + location.getLatitude();
            String longitude = "Longitude: " + location.getLongitude();
            double lat = location.getLatitude();
            double longt = location.getLongitude();
            Log.d("myLog", String.valueOf(lat));
            Log.d("myLog", String.valueOf(longt));
            curr_place = new MarkerOptions().position(new LatLng(lat,longt)).title("Current Location");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        //google told me to do this
        gMap.setMyLocationEnabled(true);
        gMap.setOnMyLocationButtonClickListener(this);
        gMap.setOnMyLocationClickListener(this);


        Log.d("myLog", "Added Markers");

        //add vancouver location
        gMap.addMarker(i_place);
//        gMap.addMarker(curr_place);
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }


    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
//        currentPolyline = gMap.addPolyline((PolylineOptions) values[0]);
    }

    public void gpsStatus(){
        //check if gps is enabled
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("GPS is disabled. Enable it now?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused")final int id) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused")final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this,"Current Location:\n" + location, Toast.LENGTH_LONG).show();

    }
}
