package com.example.countries;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    JSONObject settings;
    ObjectAnimator buttonSpin;
    ObjectAnimator imageScroll;
    ObjectAnimator imageScroll2;
    ImageView horizontal;
    ImageView horizontal2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //TRy to load the file
        File settingsFile = new File(getFilesDir(), "settings.json");
        if(settingsFile.exists()){
            Log.d("LOADINGSETTINGS", "Settings file was loaded");
            //If it exists, read the file and save the settings in the JSon object
            try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput("settings.json")));
                settings = new JSONObject(reader.readLine());
            }catch (Exception e) {}
        }else{
            //If the file does not exists, create the default settings
            Log.d("LOADINGSETTINGS", "Settings file was created");
            settings = new JSONObject();
            try{
                settings.put("theme", "default");
                settings.put("locale", "en-GB");
                settings.put("map_api", "default");
                settings.put("geo_api", "default");
                settings.put("animations", "on");

                //Create the file, and save the settings inside
                FileOutputStream output = openFileOutput("settings.json", Context.MODE_PRIVATE);
                output.write(settings.toString().getBytes());
                output.close();
            }catch (Exception e){}
        }

        try{
            Log.d("COUNTRIES", "onCreate: " + settings.getString("theme"));
            if(settings.getString("theme").equals("default")){
                setTheme(R.style.AppTheme);
            }else if (settings.getString("theme").equals("dark")){
                setTheme(R.style.DarkTheme);
            }
        }catch(Exception e){}

        setContentView(R.layout.activity_main);

        Button map = findViewById(R.id.map);
        Button settingsButton = findViewById(R.id.settings);
        horizontal = findViewById(R.id.horizontal);
        horizontal2 = findViewById(R.id.horizontal2);
        try{
            if(settings.getString("theme").equals("default")){
                horizontal.setImageResource(R.drawable.horizontal);

            }else if (settings.getString("theme").equals("dark")){
                horizontal.setImageResource(R.drawable.horizontaldark);
            }
        }catch(Exception e){}

        map.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                buttonSpin.start();

            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Settings.class);
                intent.putExtra("app_settings", settings.toString());
                startActivity(intent);

            }
        });

        buttonSpin = ObjectAnimator.ofFloat(map, "rotation", 360);
        buttonSpin.setDuration(1000);
        buttonSpin.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                intent.putExtra("app_settings", settings.toString());
                startActivity(intent);
            }
        });

        imageScroll= ObjectAnimator.ofFloat(horizontal, "translationX", -4000);
        imageScroll.setDuration(8000);
        imageScroll.setInterpolator(new LinearInterpolator());
        imageScroll.setRepeatCount(10);

        imageScroll2= ObjectAnimator.ofFloat(horizontal2, "translationX", -4000);
        imageScroll2.setDuration(8000);
        imageScroll2.setInterpolator(new LinearInterpolator());
        imageScroll2.setRepeatCount(10);

    }

    @Override
    protected void onStart() {
        super.onStart();
        String previousTheme = null;
        try {
            previousTheme = settings.getString("theme");
        } catch (Exception e) {

        }
        File settingsFile = new File(getFilesDir(), "settings.json");
        if(settingsFile.exists()) {
            Log.d("LOADINGSETTINGS", "Settings file was loaded");
            //If it exists, read the file and save the settings in the JSon object
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput("settings.json")));
                settings = new JSONObject(reader.readLine());

                if (!previousTheme.equals(settings.getString("theme"))) {
                    recreate();
                }
            } catch (Exception e) {
            }

            horizontal.setMaxWidth(50000);
            imageScroll.start();
            imageScroll2.start();
        }

    }
}
