package com.example.lesgo.wheretogo;

import android.os.Looper;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.params.HttpConnectionParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ContributeActivity extends AppCompatActivity {

    Button submit;
    EditText eName,eAddress,eLat,eLong,eDesc;

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

                    Log.d("result",makeRequest(data.toString()));

                   // Post(data.toString());
                    //Log.d("aaa",data.toString());

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }


    public static String makeRequest(String json) {
        HttpURLConnection urlConnection;
        //String url;
        String data = json;
        String result = null;
        try {
            //Connect
            urlConnection = (HttpURLConnection) ((new URL("https://powerful-escarpment-79209.herokuapp.com/api/place").openConnection()));
            urlConnection.setDoOutput(true);
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



}
