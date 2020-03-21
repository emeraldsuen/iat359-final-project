package com.example.iat359_finalapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.security.acl.Permission;
import java.util.Arrays;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback,
        GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, PlaceSelectionListener {

    private GoogleMap gMap;
    private MarkerOptions i_place, f_place, curr_place;
    private double p_latitude, p_longitude;
    private Polyline currentPolyline;
    Button getDirButton;
    LocationManager locationManager;
    AutocompleteSupportFragment autocompleteSupportFragment;
    private TextView displayDistance, displayCurrLat, displayCurrLong, displayDestLat, displayDestLong;

    double currLat, currLong;
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        displayDistance = (TextView) findViewById(R.id.displayDistance);
        displayCurrLat = (TextView) findViewById(R.id.displayCurrLat);
        displayCurrLong = (TextView) findViewById(R.id.displayCurrLong);
        displayDestLat = (TextView) findViewById(R.id.displayDestLat);
        displayDestLong = (TextView) findViewById(R.id.displayDestLong);

        //button
        getDirButton = (Button) findViewById(R.id.getDirection_button);
        getDirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastLocation();
                //get route
                displayCurrLat.setText("" + currLat);
                displayCurrLong.setText("" + currLong);
                displayDestLat.setText("" + p_latitude);
                displayDestLong.setText("" + p_longitude);
                detectDist(currLat, currLong, p_latitude, p_longitude);
            }
        });

        //setting up the places
        i_place = new MarkerOptions().position(new LatLng(49.282730, -123.120735)).title("Location 1");
        f_place = new MarkerOptions().position(new LatLng(49.248810, -122.980507)).title("Location 2");


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(MapsActivity.this);

        String url = getUrl(i_place.getPosition(), f_place.getPosition(), "driving");
        new FetchURL(MapsActivity.this).execute(url, "driving");

        //getting current location
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (gMap != null) {
                gMap.setMyLocationEnabled(true);
            }
        } else {
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION, true);
        }


        //Search Location
        autocompleteSupportFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteSupportFragment.setOnPlaceSelectedListener(this);

        String apiKey = getString(R.string.api_key);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
        PlacesClient placesClient = Places.createClient(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        //google told me to do this
        gMap.setMyLocationEnabled(true);
        gMap.setOnMyLocationButtonClickListener(this);
        gMap.setOnMyLocationClickListener(this);

        Log.d("myLog", "Added Markers");
        //add vancouver location

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

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current Location:\n" + location, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onPlaceSelected(@NonNull Place place) {
//        Toast.makeText(this,"Place chosen: " + place.getLatLng().longitude, Toast.LENGTH_SHORT).show();
        p_latitude = place.getLatLng().latitude;
        p_longitude = place.getLatLng().longitude;
        i_place = new MarkerOptions().position(new LatLng(p_latitude, p_longitude)).title("Location 1");
        gMap.addMarker(i_place);
    }

    @Override
    public void onError(@NonNull Status status) {

    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Granted. Start getting the location information
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    displayCurrLat.setText(location.getLatitude() + "");
                                    displayCurrLong.setText(location.getLongitude() + "");
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            displayCurrLat.setText(mLastLocation.getLatitude() + "");
            displayCurrLong.setText(mLastLocation.getLongitude() + "");
        }
    };

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }


//    public void detectDist(double currLat, double currLong, double destLat, double destLong) {
//        final int R = 6371; // Radius of the earth
//
//        double latDistance = Math.toRadians(destLat - currLat);
//        double lonDistance = Math.toRadians(destLong - currLong);
//        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
//                + Math.cos(Math.toRadians(currLat)) * Math.cos(Math.toRadians(destLat))
//                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
//        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//        double distance = R * c * 1000; // convert to meters
//
////        return distance;
//        displayDistance.setText("" + distance);
//    }

    public void detectDist(double currLat, double currLong, double destLat, double destLong) {
        double theta = currLong - destLong;
        double dist = Math.sin(Math.toRadians(currLat)) *
                Math.sin(Math.toRadians(destLat)) +
                Math.cos(Math.toRadians(currLat)) *
                        Math.cos(Math.toRadians(destLat)) *
                        Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;

//        return distance;
        displayDistance.setText("" + dist);
    }

//    public void detectDist(double currLat, double currLong, double destLat, double destLong) {
//        double radius = 6371;
//        double dLat = Math.toRadians(destLat-currLat);
//        double dLon = Math.toRadians(destLong-currLong);
//        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
//                Math.cos(Math.toRadians(currLat)) * Math.cos(Math.toRadians(destLat)) *
//                        Math.sin(dLon/2) * Math.sin(dLon/2);
//        double c = 2 * Math.asin(Math.sqrt(a));
//        double distance = radius * c;
//
//        displayDistance.setText("" + distance);
//    }


}
