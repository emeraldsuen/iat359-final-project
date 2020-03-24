package com.example.iat359_finalapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

public class HistoryActivity extends AppCompatActivity {

    RecyclerView historyRec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        historyRec = (RecyclerView) findViewById(R.id.history_recyclerView);
    }
}
