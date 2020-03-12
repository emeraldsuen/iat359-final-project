package com.example.iat359_finalapp;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback, View.OnClickListener {

    private GoogleMap gMap;
    private MarkerOptions i_place, f_place;
    private Polyline currentPolyline;
    Button getDirButton;

    private LocationManager locationManager = null;
    private LocationListener locationListener = null;

    private Button btnGetLocation = null;
    private EditText editLocation = null;

    private static final String TAG = "Debug";
    private Boolean flag = false;

    private double currLat, currLong;
    private TextView currLatTextView, currLongTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        btnGetLocation = (Button) findViewById(R.id.getDirection_button);
        btnGetLocation.setOnClickListener(this);

        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);


        currLatTextView = (TextView) findViewById(R.id.currLat);
        currLongTextView = (TextView) findViewById(R.id.currLong);


//      START OF MARKER CODE
        //button
//        getDirButton = (Button) findViewById(R.id.getDirection_button);
//        getDirButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //get route
//            }
//        });

        //setting up the places
        i_place = new MarkerOptions().position(new LatLng(currLat, currLong)).title("Location 1");
        f_place = new MarkerOptions().position(new LatLng(27.667491, 85.3208583)).title("Location 2");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(MapsActivity.this);
//        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
//        mapFragment.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(GoogleMap googleMap) {
//                gMap = googleMap;
//            }
//        });
        String url = getUrl(i_place.getPosition(), f_place.getPosition(), "driving");
        new FetchURL(MapsActivity.this).execute(url, "driving");

//      END OF MARKER CODE

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        Log.d("myLog", "Added Markers");
        //add markers from initial destination to final
        System.out.println("added");
        gMap.addMarker(i_place);
        gMap.addMarker(f_place);

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
        currentPolyline = gMap.addPolyline((PolylineOptions) values[0]);
    }

    @Override
    public void onClick(View v) {
//      checks if GPS is active/on
        flag = displayGpsStatus();
        if (flag) {

            Log.v(TAG, "onClick");

            Toast.makeText(getApplicationContext(), "SUCCESS: GPS is on.", Toast.LENGTH_SHORT).show();

//            locationListener = new MapsActivity.MyLocationListener();

            Log.i(TAG, "YES I DIDNT DIE");

            currLatTextView.setText("" + currLat);
            currLongTextView.setText("" + currLong);


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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);

//            locationMangaer.requestLocationUpdatesUpdates(LocationManager
//                    .GPS_PROVIDER, 5000, 10, locationListener);
            Log.i(TAG, "DATA LOC: " + currLat + ", " + currLong);

        } else {
            Toast.makeText(getApplicationContext(),"ERROR: GPS is off.",Toast.LENGTH_SHORT).show();
        }

    }

    /*----Method to Check GPS is enable or disable ----- */
    private Boolean displayGpsStatus() {
        ContentResolver contentResolver = getBaseContext()
                .getContentResolver();
        boolean gpsStatus = Settings.Secure
                .isLocationProviderEnabled(contentResolver,
                        LocationManager.GPS_PROVIDER);
        if (gpsStatus) {
            return true;

        } else {
            return false;
        }
    }

//    @Override
//    public void onLocationChanged(Location loc) {
//        editLocation.setText("");
//        Toast.makeText(getBaseContext(),"Location changed : Lat: " +
//                        loc.getLatitude()+ " Lng: " + loc.getLongitude(),
//                Toast.LENGTH_SHORT).show();
//        String longitude = "Longitude: " +loc.getLongitude();
//        Log.i(TAG, longitude);
//        String latitude = "Latitude: " +loc.getLatitude();
//        Log.i(TAG, latitude);
//
//        currLat = loc.getLatitude();
//        currLong = loc.getLongitude();
//
//        Log.i(TAG, "DATA LOC: " + currLat + ", " + currLong);
//
//        currLatTextView.setText("" + currLat);
//        currLongTextView.setText("" + currLong);
//    }
//
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//
//    }
//
//    @Override
//    public void onProviderEnabled(String provider) {
//
//    }
//
//    @Override
//    public void onProviderDisabled(String provider) {
//
//    }


    /*----------Listener class to get coordinates ------------- */
    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(android.location.Location loc) {


            editLocation.setText("");
            Toast.makeText(getBaseContext(),"Location changed : Lat: " +
                            loc.getLatitude()+ " Lng: " + loc.getLongitude(),
                    Toast.LENGTH_SHORT).show();
            String longitude = "Longitude: " +loc.getLongitude();
            Log.i(TAG, longitude);
            String latitude = "Latitude: " +loc.getLatitude();
            Log.i(TAG, latitude);

            currLat = loc.getLatitude();
            currLong = loc.getLongitude();

            Log.i(TAG, "DATA LOC: " + currLat + ", " + currLong);

            currLatTextView.setText("" + currLat);
            currLongTextView.setText("" + currLong);

            /*----------to get City-Name from coordinates ------------- */
//            String cityName=null;
//            Geocoder gcd = new Geocoder(getBaseContext(),
//                    Locale.getDefault());
//            List<Address> addresses;
//            try {
//                addresses = gcd.getFromLocation(loc.getLatitude(), loc
//                        .getLongitude(), 1);
//                if (addresses.size() > 0)
//                    System.out.println(addresses.get(0).getLocality());
//                cityName=addresses.get(0).getLocality();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            String s = longitude+"\n"+latitude +
//                    "\n\nMy Currrent City is: "+cityName;
//            editLocation.setText(s);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStatusChanged(String provider,
                                    int status, Bundle extras) {
            // TODO Auto-generated method stub
        }
    }
}
