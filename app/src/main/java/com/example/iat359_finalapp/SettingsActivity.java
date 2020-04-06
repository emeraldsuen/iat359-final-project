package com.example.iat359_finalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    EditText usernameEditText, passwordEditText;
    Button submitButton;
    SharedPreferences sharedPref;
    public static final String DEFAULT = "not available";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(this);

        SharedPreferences shareP = getSharedPreferences(getResources().getString(R.string.SharePreferenceKey), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shareP.edit();
    }

    @Override
    public void onClick(View v) {
        //saves un/pw to shared preferences
//        SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPrefs.edit();
//        editor.putString("username", usernameEditText.getText().toString());
//        editor.putString("password", passwordEditText.getText().toString());
//        Toast.makeText(this, "Username and password saved to Preferences", Toast.LENGTH_LONG).show();
//        editor.commit();
//
//        Intent intent= new Intent(this, ProfileActivity.class);
//        startActivity(intent);


        SharedPreferences sharedPrefs = getSharedPreferences(getResources().getString(R.string.SharePreferenceKey), Context.MODE_PRIVATE);
        String username = sharedPrefs.getString("username", DEFAULT);
        String password = sharedPrefs.getString("password", DEFAULT);

        if (username.equals(usernameEditText.getText().toString())&& password.equals(passwordEditText.getText().toString()))
        {
            Toast.makeText(getApplicationContext(), "Data retrieve success", Toast.LENGTH_LONG).show();
            Intent i= new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(i);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "No data found... Adding new User", Toast.LENGTH_LONG).show();
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString("Username", usernameEditText.getText().toString());
            editor.putString("Password", passwordEditText.getText().toString());
            editor.commit();
            Toast.makeText(getApplicationContext(), "Username and password saved to Preferences", Toast.LENGTH_LONG).show();
            Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(i);
        }
    }
}
