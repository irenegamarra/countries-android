package com.example.countries;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;


public class Settings extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    ImageView image;
    Spinner themeSpinner;
    Spinner animationsSpinner;



    JSONObject settings;
    String[] themeOptions = new String[]{"Default Theme", "Dark Theme"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            settings = new JSONObject(getIntent().getStringExtra("app_settings"));

            try{
                Log.d("COUNTRIES", "onCreate: " + settings.getString("theme"));
                if(settings.getString("theme").equals("default")){
                    setTheme(R.style.AppTheme);
                }else if (settings.getString("theme").equals("dark")){
                    setTheme(R.style.DarkTheme);
                }
            }catch(Exception e){}


            setContentView(R.layout.settings);

            image = findViewById(R.id.image);
            try {
                if(settings.getString("theme").equals("default")){
                    image.setImageResource(R.drawable.settings_25);

                }else if (settings.getString("theme").equals("dark")){
                    image.setImageResource(R.drawable.settings_dark);
                }
            } catch (JSONException e) {

            }


            themeSpinner = findViewById(R.id.themeSpinner);
            animationsSpinner = findViewById(R.id.animationsSpinner);
            EditText apikeyEditText = findViewById(R.id.apikeyEditText);
            EditText geoEditText = findViewById(R.id.geoEditText);

            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, themeOptions);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          //  animationsSpinner.setAdapter(spinnerAdapter);
          //  animationsSpinner.setOnItemSelectedListener(this);
            themeSpinner.setAdapter(spinnerAdapter);
            themeSpinner.setOnItemSelectedListener(this);


            if (settings.getString("map_api").equals("default")) {
                apikeyEditText.setText(R.string.mapsapikey);
            } else {
                apikeyEditText.setText(settings.getString("map_api"));
            }


            if(settings.getString("geo_api").equals("default")){
                geoEditText.setText(R.string.reversegeoapikey);

            }else{
                geoEditText.setText(settings.getString("geo_api"));
            }
//
//
//            if(settings.getString("animationsSpinner").equals("on")){
//                animationsSpinner.setSpinner(R.string.reversegeoapikey);
//
//            }else{
//                geoEditText.setText(settings.getString("geo_api"));
//            }

        } catch (JSONException e) {

        }


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        try {
            if (parent.getId() == R.id.themeSpinner) {
                if (position == 0) {
                    settings.put("theme", "default");
                }
                if (position == 1) {
                    Log.d("COUNTRIES", "onItemSelected: writing dark");
                    settings.put("theme", "dark");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onPause() {
        try {
            FileOutputStream output = openFileOutput("settings.json", Context.MODE_PRIVATE);
            output.write(settings.toString().getBytes());
            output.close();
            Log.d("COUNTRIES", "onCreate: " + settings.getString("theme"));
        } catch (Exception e) {

        }
        super.onPause();
    }
}
