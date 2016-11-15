package com.example.lesgo.wheretogo;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    List<Place> place_list = new ArrayList<>();
    CustomAdapter adapter;
    RecyclerView recy_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try{
            int count = 0;
            JSONArray alldata= getAllPlaces();

            for(int i=0; i< alldata.length();i++){
                JSONObject pointed = alldata.getJSONObject(i);

                String name,address,longt,lat,desc;
                name= pointed.getString("name");
                address = pointed.getString("address");
                longt = pointed.getString("long");
                lat = pointed.getString("lat");
                desc = pointed.getString("desc");

                Place place = new Place(name,address,longt,lat,desc);

                place_list.add(place);

                //Log.d("title", pointed.getString("name"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        adapter = new CustomAdapter(place_list);
        adapter.setOnItemClickListener(new CustomAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(Place place) {

            }
        });

        recy_view = (RecyclerView)findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recy_view.setLayoutManager(layoutManager);
        recy_view.setItemAnimator(new DefaultItemAnimator());
        recy_view.setAdapter(adapter);


    }

   // private JSONObject getAllPlaces() throws IOException {
    private JSONArray getAllPlaces() throws IOException {

        InputStream is = null;
        JSONArray obj = new JSONArray();


        String link = "https://powerful-escarpment-79209.herokuapp.com/api/place";
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
                obj = new JSONArray(stringBuilder.toString());
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

}
