package com.example.lesgo.wheretogo;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ContributeActivity extends AppCompatActivity {

    Button submit;
    EditText eName,eAddress,eLat,eLong,eDesc;
    String lat,longt;
    LocationManager locationManager;
    LocationListener llistener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contribute);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        submit = (Button)findViewById(R.id.submit);
        eName = (EditText)findViewById(R.id.name);
        eAddress= (EditText)findViewById(R.id.address);
        eLat= (EditText)findViewById(R.id.latitude);
        eLong= (EditText)findViewById(R.id.longitude);
        eDesc= (EditText)findViewById(R.id.description);


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        llistener = new LocationListener() {
            public void onLocationChanged(Location loc) {
                // Called when a new location is found by the network location provider.
                lat = String.valueOf(loc.getLatitude());
                longt = String.valueOf(loc.getLongitude());
               /* Toast.makeText(
                        getBaseContext(),
                        "Location changed: Lat: " + loc.getLatitude() + " Lng: "
                                + loc.getLongitude(), Toast.LENGTH_SHORT).show();*/

                eLat.setText(lat);
                eLong.setText(longt);

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, llistener);




        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject data = new JSONObject();

                try{
                    data.put("name", eName.getText().toString());
                    data.put("address",eAddress.getText().toString());
                    data.put("lat",eLat.getText().toString());
                    data.put("long",eLong.getText().toString());
                    data.put("desc",eDesc.getText().toString());


                    JSONObject aaa;
                    aaa = callApi(lat,longt);

                    JSONArray result = aaa.getJSONArray("results");
                    JSONObject first = result.getJSONObject(0);
                    String address = first.getString("formatted_address");

                    Log.d("address",address);
                    Toast.makeText(getBaseContext(),"Location" + address,Toast.LENGTH_SHORT).show();

                   // Post(data.toString());

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    private void Post(String data){

        HttpURLConnection httpcon;
        String url = "https://powerful-escarpment-79209.herokuapp.com/api/place";
        //String data = null;
        String result = null;
        try {
            //Connect
            httpcon = (HttpURLConnection) ((new URL(url).openConnection()));
            httpcon.setDoOutput(true);
            // httpcon.setRequestProperty("Content-Type", "application/json");
            httpcon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpcon.setRequestProperty("Accept", "application/json");
            httpcon.setRequestMethod("POST");
            httpcon.connect();

            //Write
            OutputStream os = httpcon.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(data);
            writer.close();
            os.close();

            Log.d("done","posted");


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private JSONObject callApi(String lat,String longt) throws IOException {
        InputStream is = null;
        JSONObject obj = new JSONObject();


        String link = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+ lat +","+ longt + "&key=AIzaSyAYIBpLtL-NFfV_3mepgb1cASQo8xlTaZs";
        try{
            StringBuilder stringBuilder = new StringBuilder();

            URL url = new URL(link);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 );
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);


            conn.connect();
            int response = conn.getResponseCode();
            Log.d("RESPONSE", "The response is: " + response);
            is = conn.getInputStream();

            int b;
            while((b = is.read())!= -1){
                stringBuilder.append((char)b);
            }

            try{
                obj = new JSONObject(stringBuilder.toString());
            } catch (JSONException e)
            {
                e.printStackTrace();
            }

        } finally {
            if (is != null) {
                is.close();
            }
        }
        return obj;

    }

    @Override
    protected void onPause() {
        super.onPause();
       // locationManager.removeUpdates(llistener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(llistener);

    }

    @Override
    protected void onPostResume() {

        super.onPostResume();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, llistener);


    }
}
