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
    SeekBar distSeekBar;
    RadioButton vibrate, alarm, headphones;
    Button toneButton;
    Place destination;

    boolean inTransit = false;

    Button startButton;

    MyDatabase db;

    String dest_name;
    int dist_km;

    String outputType;
    int progressChangedValue = 0;
    String s;

    double dest_lat, dest_long;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        Intent i = getIntent();

        dest_lat =  i.getDoubleExtra("DESTINATION_LAT", 0.0);
        dest_long = i.getDoubleExtra("DESTINATION_LONG", 0.0);

        destinationNameTextView = (TextView) findViewById(R.id.locationText);
        dest_name = i.getStringExtra("DESTINATION_STRING");
        destinationNameTextView.setText(dest_name);

        distFromDestTextView = (TextView) findViewById(R.id.distanceNum_TextView);
        dist_km = (int) i.getDoubleExtra("DESTINATION_DISTANCE", 0.0);
//        distFromDestTextView.setText("Distance: " + dist_km);
        distFromDestTextView.setText("");


        distSeekBar = (SeekBar) findViewById(R.id.distance_seek);
        distSeekBar.setMax(dist_km);
        distSeekBar.setProgress(dist_km / 2);

        vibrate = (RadioButton) findViewById(R.id.vibrate_Radio);
        alarm = (RadioButton) findViewById(R.id.alarm_Radio);
        headphones = (RadioButton) findViewById(R.id.headphones_Radio);

        toneName = (TextView) findViewById(R.id.audio_name);

        toneButton = (Button) findViewById(R.id.ringtone_button);
        toneButton.setOnClickListener(this);
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


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 6) {
            Uri i = data.getData();  // getData
            s = i.getPath(); // getPath
            File k = new File(s);  // set File from path

            toneName.setText(s);
            Log.i(TAG, s);

//            if (s != null) {      // file.exists
//
//                ContentValues values = new ContentValues();
//                values.put(MediaStore.MediaColumns.DATA, k.getAbsolutePath());
//                values.put(MediaStore.MediaColumns.TITLE, "ring");
//                values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
//                values.put(MediaStore.MediaColumns.SIZE, k.length());
//                values.put(MediaStore.Audio.Media.ARTIST, R.string.app_name);
//                values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
//                values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
//                values.put(MediaStore.Audio.Media.IS_ALARM, true);
//                values.put(MediaStore.Audio.Media.IS_MUSIC, true);
//
//
//                Uri uri = MediaStore.Audio.Media.getContentUriForPath(k.getAbsolutePath());
//                getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + k.getAbsolutePath() + "\"", null);
//                Uri newUri = getContentResolver().insert(uri, values);
//
//                try {
//                    RingtoneManager.setActualDefaultRingtoneUri(
//                            SetupActivity.this, RingtoneManager.TYPE_RINGTONE,
//                            newUri);
//                } catch (Throwable t) {
//
//                }
//            }
        }
    }

//    public String getRealPathFromURI(Context context, Uri contentUri) {
//        Cursor cursor = null;
//        try {
//            String[] proj = {MediaStore.Images.Media.DATA};
//            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
//            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            cursor.moveToFirst();
//            return cursor.getString(column_index);
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//        }
//    }

    @Override
    public void onClick(View v) {
        //starting trip
        if (v.getId() == R.id.startTrip_Button) {
            if (vibrate.isChecked()) {
                outputType = "vibrate";
            } else if (alarm.isChecked()) {
                outputType = "alarm";
            } else if (headphones.isChecked()) {
                outputType = "headphones";
            } else {
                Toast.makeText(this, "Please select a output source.", Toast.LENGTH_SHORT).show();
            }

            if (s == null) {
                Toast.makeText(this, "Please select a ringtone.", Toast.LENGTH_SHORT).show();
            }

            String name = dest_name;
            String type = "transit";
            dist_km = (int) dist_km;
//            String distance = String.valueOf(dist_km) + "km";
            String distance = progressChangedValue + "km";
            String ringtone = s;

            long id = db.insertData(name, type, distance, ringtone);
            if (id < 0) {
                Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
            }


            inTransit = true;

            Intent i = new Intent(SetupActivity.this, MapsActivity.class);

            i.putExtra("InTransit", inTransit);
            i.putExtra("Dest_lat", dest_lat);
            i.putExtra("Dest_long", dest_long);
            i.putExtra("Dest_name", name);
            i.putExtra("Dest_dist", dist_km);

            startActivity(i);
        } else if (v.getId() == R.id.ringtone_button) {
            Toast.makeText(this, "ringtone", Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent();
            intent1.setAction(Intent.ACTION_GET_CONTENT);
            intent1.setType("audio/*");
            startActivityForResult(
                    Intent.createChooser(intent1, "Choose Sound File"), 6);
        }
    }
}
