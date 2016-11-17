package com.example.lesgo.wheretogo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Looper;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.apache.commons.io.output.ByteArrayOutputStream;
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
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

public class ContributeActivity extends AppCompatActivity {

    Button submit,picture;
    EditText eName,eAddress,eLat,eLong,eDesc;
    String lat,longt;
    LocationManager locationManager;
    LocationListener llistener;
    Bitmap selectedImage;
    ImageView img;
    String selectedImg = "";
    TextView gps;
    Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contribute);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        submit = (Button)findViewById(R.id.submit);
        eName = (EditText)findViewById(R.id.name);
        eAddress= (EditText)findViewById(R.id.address);
        eAddress.setText("lorong bayur bukit 18 93350 kuching sarawak");
        eLat= (EditText)findViewById(R.id.latitude);
        eLong= (EditText)findViewById(R.id.longitude);
        eDesc= (EditText)findViewById(R.id.description);
        picture = (Button)findViewById(R.id.picture) ;
        img = (ImageView)findViewById(R.id.img1);
        gps = (TextView)findViewById(R.id.gpsabc);
        spinner = (Spinner)findViewById(R.id.planets_spinner);

        String[] items = new String[]{"Restaurant","Shopping mall","Accomodation", "Local attractions"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);


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
                gps.setText("Activated");
                eLat.setText(lat);
                eLong.setText(longt);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };


        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });
        // Register the listener with the Location Manager to receive location updates
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, llistener);

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
                    data.put("image",selectedImg.replace("=",""));
                    data.put("category",spinner.getSelectedItem().toString());

                    Log.d("aaa0",spinner.getSelectedItem().toString());

                    //Log.d("img",selectedImg);
                   // data.put("image","testing abc");

                    JSONObject aaa;
                    aaa = callApi(lat,longt);

                    JSONArray result = aaa.getJSONArray("results");
                    JSONObject first = result.getJSONObject(0);
                    String address = first.getString("formatted_address");

                  //  Log.d("address",address);
                    String sample = address.replace(",", "");
                    String[] elements = sample.split(" ");

                    String input = eAddress.getText().toString();
                    input = input.replace(",", "");
                    String[] inputs = input.split(" ");

                    Boolean add = checkAddress(elements,inputs,elements.length);

                    System.out.println("after compress:");

                    if(add)
                    {
                       Toast.makeText(getBaseContext(),"Address is correct",Toast.LENGTH_SHORT).show();

                        new PostData().execute(data);
                        //makeRequest(data.toString());
                       // Log.d("result",makeRequest(data.toString()));

                    }
                    else{
                        Toast.makeText(getBaseContext(),"Either you have enterred an incorrect address or you are not at the correct location",Toast.LENGTH_SHORT).show();
                    }


                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }




    private boolean checkAddress(String[] address, String[] input,int length){
        int i = 0;

        for(String a:address)
        {
            for(String b:input)
            {
                if(a.equalsIgnoreCase(b))
                {
                    i+=1;
                    break;
                }
            }
        }

        double rounded = Math.round((double)length/2);
        Log.d("round",String.valueOf(rounded) + "i is " + String.valueOf(i));

        if(i >= rounded -1)
            return true;
        else
            return false;
    }

    public static String postRequest(String json) {
        HttpURLConnection urlConnection;
        //String url;
        String data = json;
        String result = null;
        try {
            //Connect
            urlConnection = (HttpURLConnection) ((new URL("https://powerful-escarpment-79209.herokuapp.com/api/place").openConnection()));
            urlConnection.setDoOutput(true);
           // urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestMethod("POST");
            urlConnection.connect();

            //Write
            OutputStream outputStream = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(data);
            writer.close();
            outputStream.close();

            //Read
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

            String line = null;
            StringBuilder sb = new StringBuilder();

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            bufferedReader.close();
            result = sb.toString();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
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
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(llistener);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, llistener);
    }

    class PostData extends AsyncTask<JSONObject,Boolean,Boolean>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(JSONObject... params) {
            JSONObject data;
            data = params[0];

            String result = postRequest(data.toString());
            JSONObject resultobj;
            try{
                resultobj = new JSONObject(result);
                //Log.d("result",resultobj.toString());

                if(data.getString("name").equalsIgnoreCase(resultobj.getString("name")) &&
                data.getString("lat").equalsIgnoreCase(resultobj.getString("lat")) &&
                data.getString("long").equalsIgnoreCase(resultobj.getString("long")) &&
                data.getString("address").equalsIgnoreCase(resultobj.getString("address")))
                {
                    return true;
                }


            }catch (Exception e)
            {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean)
            {
                Toast.makeText(getBaseContext(),"Successfully contributed",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getBaseContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK) {

            try{
                InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
                selectedImage = BitmapFactory.decodeStream(inputStream);
                img.setImageBitmap(selectedImage);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 50, baos);

                byte[] b = baos.toByteArray();
                selectedImg = Base64.encodeToString(b, Base64.NO_WRAP);

                //Log.d("encoded",selectedImg);

            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    }
}
