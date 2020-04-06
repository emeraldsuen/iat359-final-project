package com.example.iat359_finalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    EditText nameEdit, phoneEdit, addressEdit;
    TextView travelTimeText;
    Button saveBtn;
    SharedPreferences.Editor editor;

    public static final String DEFAULT = "not available";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameEdit = (EditText)findViewById(R.id.profileNameEditText);
        phoneEdit = (EditText)findViewById(R.id.phoneNumEditText);
        addressEdit = (EditText)findViewById(R.id.profileAddressEditText);
        travelTimeText = (TextView)findViewById(R.id.travelTimeTextView);
        saveBtn = (Button)findViewById(R.id.saveButton);
        saveBtn.setOnClickListener(this);

        SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        editor = sharedPrefs.edit();

        //set up prev data
        if(sharedPrefs.contains("name")){
            nameEdit.setText(sharedPrefs.getString("name", ""));
        }
        if(sharedPrefs.contains("phone")){
            phoneEdit.setText(sharedPrefs.getString("phone", ""));
        }
        if(sharedPrefs.contains("address")){
            addressEdit.setText(sharedPrefs.getString("address", ""));
        }
        if(sharedPrefs.contains("totalDist")){
            travelTimeText.setText(Integer.toString(sharedPrefs.getInt("totalDist", 0)));
        }
    }

    @Override
    public void onClick(View v) {
        String name = nameEdit.getText().toString();
        editor.putString("name", name);
        String phone = phoneEdit.getText().toString();
        editor.putString("phone",phone);
        String address = addressEdit.getText().toString();
        editor.putString("address", address);

        editor.commit();
    }
}
