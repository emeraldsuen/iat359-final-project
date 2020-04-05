package com.example.iat359_finalapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    RecyclerView myRecycler;
    MyDatabase db;
    MyAdapter myAdapter;
    MyHelper myHelper;
    private RecyclerView.LayoutManager myLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        myRecycler = (RecyclerView) findViewById(R.id.history_recyclerView);
        myLayoutManager = new LinearLayoutManager(this);
        myRecycler.setLayoutManager(myLayoutManager);

        db = new MyDatabase(this);
        myHelper = new MyHelper(this);

        Cursor cursor = db.getData();

        int index1 = cursor.getColumnIndex(Constants.NAME);
        int index2 = cursor.getColumnIndex(Constants.TYPE);
        int index3 = cursor.getColumnIndex(Constants.DISTANCE);
        int index4 = cursor.getColumnIndex(Constants.OUTPUT);
        int index5 = cursor.getColumnIndex(Constants.VOLUME);
        int index6 = cursor.getColumnIndex(Constants.VIBRATE);
        int index7 = cursor.getColumnIndex(Constants.LAT);
        int index8 = cursor.getColumnIndex(Constants.LON);

        ArrayList<String> mArrayList = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String routeName = cursor.getString(index1);
            String routeType = cursor.getString(index2);
            String routeDist = cursor.getString(index3);
            String routeOutput = cursor.getString(index4);
            String routeVolume = cursor.getString(index5);
            String routeVibrate = cursor.getString(index6);
            String routeLat = cursor.getString(index7);
            String routeLon = cursor.getString(index8);
            String s = routeName + "," + routeType + "," + routeDist + "," + routeOutput + "," + routeVolume + "," + routeVibrate + "," + routeLat + "," + routeLon;
            mArrayList.add(s);
            cursor.moveToNext();
        }

        myAdapter = new MyAdapter(mArrayList);
        myRecycler.setAdapter(myAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LinearLayout clickedRow = (LinearLayout) view;
        TextView routeNameTextView = (TextView) view.findViewById(R.id.routeNameEntry);
        TextView routeTypeTextView = (TextView) view.findViewById(R.id.routeTypeEntry);
        TextView routeDistTextView = (TextView) view.findViewById(R.id.routeDistEntry);
        TextView routeOutputTextView = (TextView) view.findViewById(R.id.routeOutputEntry);
        TextView routeVolumeTextView = (TextView) view.findViewById(R.id.routeVolumeEntry);
        TextView routeVibrateTextView = (TextView) view.findViewById(R.id.routeVibrateEntry);
        TextView routeLatTextView = (TextView) view.findViewById(R.id.routeLatEntry);
        TextView routeLonTextView = (TextView) view.findViewById(R.id.routeLonEntry);

        Toast.makeText(this, "row " + (1 + position) + ":  " + routeNameTextView.getText() + " " + routeTypeTextView.getText() + " " + routeDistTextView.getText() + " " + routeOutputTextView.getText() + " " + routeVolumeTextView.getText() + " " + routeVibrateTextView.getText() + " " + routeLatTextView.getText() + " " + routeLonTextView.getText(), Toast.LENGTH_LONG).show();
    }
}
