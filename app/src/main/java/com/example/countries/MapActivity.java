package com.example.countries;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    JSONObject settings = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try{
            settings = new JSONObject(getIntent().getStringExtra("app_settings"));
        }catch(Exception e){
            e.printStackTrace();
        }
        try{
            if(settings.getString("theme").equals("default")){

            }else if (settings.getString("theme").equals("dark")){
                setTheme(R.style.DarkTheme);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        setContentView(R.layout.map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap map = googleMap;
        try{
            if(settings.getString("theme").equals("default")){
                map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstylelight));

            }else if (settings.getString("theme").equals("dark")){
                map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                (new ReverseGEONetworkTask()).execute(latLng);
            }
        });



    }

    public void displayMessage (String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setMessage(message);
        builder.setTitle("Click location");
        builder.create().show();
    }




    private class ReverseGEONetworkTask extends AsyncTask<LatLng, Void, JSONObject> {

        private String apikey = getResources().getString(R.string.reversegeoapikey);
        private String url = "https://eu1.locationiq.com/v1/reverse.php";

        @Override
        protected JSONObject doInBackground(LatLng ...positions) {

            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(
                    url + "?key=" + apikey + "&lat=" + positions[0].latitude + "&lon=" + positions[0].longitude + "&format=json"
                ).openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String smallChunk;
                StringBuilder builder = new StringBuilder();
                while ((smallChunk = reader.readLine()) != null) {
                    builder.append(smallChunk);
                }
                reader.close();
                connection.disconnect();

                String finalResult = builder.toString();

                return new JSONObject(finalResult);

            } catch (MalformedURLException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();

            }catch (JSONException e){
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try{
                (new CountryInfoNetworkTask()). execute(jsonObject.getJSONObject("address").getString("country"));
            }catch(Exception e){
                e.printStackTrace();
            }

        }
    }


  private class CountryInfoNetworkTask extends AsyncTask<String, Void, JSONArray> {

    private String url = "https://restcountries.eu/rest/v2/name/";

    @Override
    protected JSONArray doInBackground(String ...names) {

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url + names[0]).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String smallChunk;
            StringBuilder builder = new StringBuilder();
            while ((smallChunk = reader.readLine()) != null) {
                builder.append(smallChunk);
            }
            reader.close();
            connection.disconnect();

            String finalResult = builder.toString();

            return new JSONArray(finalResult);

        } catch (MalformedURLException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();

        }catch (JSONException e){
            e.printStackTrace();

        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONArray jsonArray) {
        try{
            Log.d("ONPOSTSECOND", "onPostExecute: " + jsonArray.toString());
            displayMessage("You clicked on " + jsonArray.getJSONObject(0).getString("name") + " which has a population of " + jsonArray.getJSONObject(0).getInt("population"));

        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
}

