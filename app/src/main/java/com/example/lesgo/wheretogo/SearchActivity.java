package com.example.lesgo.wheretogo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

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

public class SearchActivity extends AppCompatActivity implements AsyncResponse{

    List<Place> place_list = new ArrayList<>();
    CustomAdapter adapter;
    RecyclerView recy_view;

    JSONArray alldata;
    getAll task = new getAll();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        /*task.delegate = this;

        task.execute();*/


        try{
            alldata = getAllPlaces();


            for(int i=0; i< alldata.length();i++){
                JSONObject pointed = alldata.getJSONObject(i);
                JSONArray comments;

                String name,address,longt,lat,desc,img,category,id;
                name= pointed.getString("name");
                address = pointed.getString("address");
                longt = pointed.getString("long");
                lat = pointed.getString("lat");
                desc = pointed.getString("desc");
                img = pointed.getString("image");
                category = pointed.getString("category");

                comments = pointed.getJSONArray("comments");
                id = pointed.getString("_id");

                byte[] decodedString = Base64.decode(img, Base64.NO_WRAP);
                Bitmap decodedImg = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                Place place = new Place(name,address,longt,lat,desc,decodedImg,category,img,comments,id);
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

                Intent intent = new Intent();
                intent.setClass(getApplicationContext(),ShowPlaceActivity.class);
                intent.putExtra("name",place.getName());
                intent.putExtra("address",place.getAddress());
                intent.putExtra("desc",place.getDesc());
                intent.putExtra("category",place.getCategory());
                intent.putExtra("image",place.getImgstring());
                intent.putExtra("lat",place.getLat());
                intent.putExtra("long",place.getLongi());

                intent.putExtra("id",place.getId());
                intent.putExtra("comments",place.getComments().toString());

                startActivity(intent);


            }
        });

        recy_view = (RecyclerView)findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recy_view.setLayoutManager(layoutManager);
        recy_view.setItemAnimator(new DefaultItemAnimator());
        recy_view.setAdapter(adapter);


    }

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

    @Override
    public void processFinish(JSONArray arr) {
        alldata = arr;
    }

    class getAll extends AsyncTask<Void,JSONArray,JSONArray>
    {
        public AsyncResponse delegate = null;

        JSONArray arr;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONArray doInBackground(Void... params) {
            try{
                arr = getAllPlaces();

                Log.d("length",String.valueOf(arr.length()));
                if(arr.length()>0)
                {
                    return arr;
                }


            }catch (IOException e)
            {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            delegate.processFinish(jsonArray);
        }


    }



}
