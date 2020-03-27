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
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.w3c.dom.Text;

import java.security.acl.Permission;
import java.text.DecimalFormat;
import java.util.Arrays;

import static java.lang.String.valueOf;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback,
        GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, PlaceSelectionListener, View.OnClickListener, LocationListener {

    private GoogleMap gMap;
    private MarkerOptions i_place, f_place;
    private double p_latitude, p_longitude;
    Button getDirButton;
    TextView DistanceTo;
    LocationManager locationManager;
    AutocompleteSupportFragment autocompleteSupportFragment;

    private Place destination;
    Marker myMarker;
    Location currLocation;
    double curr_lat, curr_long;
    //    double lat_dist, long_dist;
    double distanceToDest;
    double distanceBeforeAlarm;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    Boolean inTransit = false;
    String outputType;
    String p_name;
    int vol;
    boolean vibrateUser;
    boolean ringing;

    Vibrator v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Log.i("TEST", "MapsActivity");

        //setting up the places
        i_place = new MarkerOptions().position(new LatLng(49.282730, -123.120735)).title("Location 1");
        f_place = new MarkerOptions().position(new LatLng(49.248810, -122.980507)).title("Location 2");


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(MapsActivity.this);

        String url = getUrl(i_place.getPosition(), f_place.getPosition(), "driving");
        new FetchURL(MapsActivity.this).execute(url, "driving");

        //getting current location
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        //check if gps is enabled
        gpsStatus();


        //You have to ask user for permission for location services
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (gMap != null) {
                gMap.setMyLocationEnabled(true);
            }
        } else {
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION, true);
        }

        Intent i2 = getIntent();

        // check if there are extras
        Bundle extras = i2.getExtras();
        if (extras == null) {

        } else {
            //get extras from SetupActivity
            inTransit = i2.getBooleanExtra("InTransit", true);
            p_latitude = i2.getDoubleExtra("Dest_lat", 0.0);
            p_longitude = i2.getDoubleExtra("Dest_long", 0.0);
            p_name = i2.getStringExtra("Dest_name");
            distanceBeforeAlarm = i2.getDoubleExtra("Dest_dist", 0.0);
            outputType = i2.getStringExtra("OUTPUT");
            vol = i2.getIntExtra("VOL", 0);
            vibrateUser = i2.getBooleanExtra("VIBRATE", false);

        }
        //button
        if (inTransit == false) {
            getDirButton = (Button) findViewById(R.id.getDirection_button);
            getDirButton.setText("Get Direction");
            getDirButton.setOnClickListener(this);
            DistanceTo = (TextView) findViewById(R.id.alarmTextView);
        } else if (inTransit == true) {
            getDirButton = (Button) findViewById(R.id.getDirection_button);
            getDirButton.setText("In Transit");
            getDirButton.setOnClickListener(this);
            DistanceTo = (TextView) findViewById(R.id.alarmTextView);
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

        if (inTransit == true) {
            i_place = new MarkerOptions().position(new LatLng(p_latitude, p_longitude)).title(p_name);
            myMarker = gMap.addMarker(i_place);
        }
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
        if (inTransit == false) {
            destination = place;
            p_latitude = place.getLatLng().latitude;
            p_longitude = place.getLatLng().longitude;
            i_place = new MarkerOptions().position(new LatLng(p_latitude, p_longitude)).title(destination.getName());
            if (myMarker == null) {
                myMarker = gMap.addMarker(i_place);
            } else if (myMarker != null) {
                myMarker.remove();
                myMarker = gMap.addMarker(i_place);
            }
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(p_latitude, p_longitude), 12.0f));
//            distanceToDest = CalculationByDistance(curr_lat, curr_long, p_latitude, p_longitude);
//            DistanceTo.setText("Distance" + distanceToDest);
        }
    }

    @Override
    public void onError(@NonNull Status status) {

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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.getDirection_button && inTransit == false) {
            //send info to setup page
            Intent i = new Intent(MapsActivity.this, SetupActivity.class);

            //if destination has been selected
            if (destination != null) {

                //calculation
                distanceToDest = CalculationByDistance(curr_lat, curr_long, p_latitude, p_longitude);

                i.putExtra("DESTINATION_STRING", destination.getName());
                i.putExtra("DESTINATION_DISTANCE", distanceToDest);
                i.putExtra("DESTINATION_LAT", p_latitude);
                i.putExtra("DESTINATION_LONG", p_longitude);
                startActivity(i);
            } else {
                Toast.makeText(this, "Please select a destination" + curr_lat + " " + curr_long, Toast.LENGTH_SHORT).show();
            }

        } else if (v.getId() == R.id.getDirection_button && inTransit == true) {
            Intent i3 = new Intent(MapsActivity.this, MainActivity.class);
            inTransit = false;
            startActivity(i3);
        }
    }

    @Override
    public void onTaskDone(Object... values) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (inTransit == true) {
            if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            } else {
                // Write you code here if permission already given.
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                currLocation = location;
                curr_lat = currLocation.getLatitude();
                curr_long = currLocation.getLongitude();
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curr_lat, curr_long), 12.0f));
                double ugh = CalculationByDistance(curr_lat, curr_long, p_latitude, p_longitude);
                DistanceTo.setText(ugh + "  " + distanceBeforeAlarm);

                //if user is close enough, sound/vibrate
                if (ugh <= distanceBeforeAlarm) {
                    if (vibrateUser == true) {
                        vibrate();
                    }
                    Log.i("TEST", outputType);
                    if (outputType.equals("alarm")) {
                        soundAlarm();
                        Log.i("TEST", "alarm ringing");
                    } else if (outputType.equals("headphones")) {
                        soundMusic();
                        Log.i("TEST", "music ringing");
                    }

                    if (ringing == false) {
                        Intent i4 = new Intent(MapsActivity.this, MainActivity.class);
                        inTransit = false;
                        startActivity(i4);
                        Toast.makeText(this, "Transit is done", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else {      //not in transit
            if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            } else {
                // Write you code here if permission already given.
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                currLocation = location;
                curr_lat = currLocation.getLatitude();
                curr_long = currLocation.getLongitude();
            }
        }
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

    private void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(400);
    }

    private void soundMusic() {
        ToneGenerator tg1 = new ToneGenerator(AudioManager.STREAM_MUSIC, vol);
        int counter = 0;
        for (int i = 0; i < 5; i++) {
            tg1.startTone(ToneGenerator.TONE_CDMA_PIP, 1000);
            counter++;
            if (counter <= 5) {
                ringing = true;
            } else {
                ringing = false;
            }
        }
    }

    private void soundAlarm() {
        ToneGenerator tg2 = new ToneGenerator(AudioManager.STREAM_ALARM, vol);
        int counter = 0;
        for (int i = 0; i < 5; i++) {
            tg2.startTone(ToneGenerator.TONE_CDMA_PIP, 1000);
            counter++;
            if (counter <= 5) {
                ringing = true;
            } else {
                ringing = false;
            }
        }
    }
}
