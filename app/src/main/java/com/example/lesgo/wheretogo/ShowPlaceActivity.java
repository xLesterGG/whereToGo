package com.example.lesgo.wheretogo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.android.rides.RideRequestButtonCallback;
import com.uber.sdk.rides.client.ServerTokenSession;
import com.uber.sdk.rides.client.SessionConfiguration;
import com.uber.sdk.rides.client.error.ApiError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONAssert;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ShowPlaceActivity extends AppCompatActivity {

    TextView address,desc,nocomment,avgrating,norating;
    ImageView img;
    Bundle bundle;
    EditText commen,username;
    JSONArray comments;
    Button addcomment;
    String namestr,categorystr,addr,descstr,imgencoded,latitude,longtitude,id,commentarray,latestrating;
    RatingBar ratingbar;
    RecyclerView recy_view;
    CommentsAdapter adapter1;

    LocationManager locationManager;
    LocationListener llistener;
    String lat,longt;

    List<Comment> comment_list = new ArrayList<>();

    RideRequestButton uberbtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_place);



        latestrating ="1";
        uberbtn = (RideRequestButton)findViewById(R.id.uber);
        uberbtn.setEnabled(false); // prevent clicking until data is loaded

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Location a = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        if(a!=null)
        {
            Log.d("null","null");
            lat = String.valueOf(a.getLatitude());
            longt = String.valueOf(a.getLongitude());
        }

        llistener = new LocationListener() {
            public void onLocationChanged(Location loc) {
                // Called when a new location is found by the network location provider.
                lat = String.valueOf(loc.getLatitude());
                longt = String.valueOf(loc.getLongitude());
                Toast.makeText(
                        getBaseContext(),
                        "Location changed: Lat: " + loc.getLatitude() + " Lng: "
                                + loc.getLongitude(), Toast.LENGTH_SHORT).show();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {
                Toast.makeText(getApplicationContext(),"GPS has been enabled",Toast.LENGTH_SHORT).show();
            }
            public void onProviderDisabled(String provider) {
                Toast.makeText(getApplicationContext(),"GPS has been disabled, please enable your GPS",Toast.LENGTH_SHORT).show();

            }
        };

        norating = (TextView)findViewById(R.id.norating);
        avgrating = (TextView)findViewById(R.id.avgrating);
        address =(TextView)findViewById(R.id.address);
        desc = (TextView)findViewById(R.id.desc);
        img  = (ImageView)findViewById(R.id.image);
        commen = (EditText)findViewById(R.id.comment);
        addcomment = (Button)findViewById(R.id.submitcomment);
        username = (EditText)findViewById(R.id.username);
        nocomment = (TextView)findViewById(R.id.nocomment);
        ratingbar = (RatingBar)findViewById(R.id.ratingBar);


        bundle = getIntent().getExtras();
        namestr = bundle.getString("name");
        categorystr = bundle.getString("category");
        addr = bundle.getString("address");
        descstr = bundle.getString("desc");
        imgencoded = bundle.getString("image");
        latitude = bundle.getString("lat");
        longtitude = bundle.getString("long");



        id = bundle.getString("id");
        setTitle(namestr);



        Log.d("iamhere","aaa" + latitude + longtitude);
        Log.d("iamhere1","bbb" + lat + longt);
        commentarray = bundle.getString("comments");
        Log.d("iamhere","aaa" + latitude + longtitude);

        address.setText(addr);
        desc.setText(descstr);

    //*************************************************************************************************************** UBER SECTION
        RideParameters rideParams = new RideParameters.Builder()
                .setPickupLocation(Double.parseDouble(lat),Double.parseDouble(longt),"Your current location",null)  // get value from gps
                .setDropoffLocation(Double.parseDouble(latitude),Double.parseDouble(longtitude),null,namestr)
                .build();

        SessionConfiguration config = new SessionConfiguration.Builder()
                .setClientId("xAeXBg-Pz_Hy6vhuLJeHRenr9dcxf2W4")
                .setServerToken("GluLkrDBR3KyNFQv_yMu2JE--_ani4E6hGGv_gCw")
                .build();

        ServerTokenSession session = new ServerTokenSession(config);
        RideRequestButtonCallback callback = new RideRequestButtonCallback() {

            @Override
            public void onRideInformationLoaded() {
                uberbtn.setEnabled(true);
            }

            @Override
            public void onError(ApiError apiError) {
                Log.d("error","error" + apiError);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d("error","error" + throwable);

            }
        };

        uberbtn.setRideParameters(rideParams);
        uberbtn.setSession(session);
        uberbtn.setCallback(callback);
        uberbtn.loadRideInformation();
        //*************************************************************************************************************** UBER SECTION

        try{
            comments = new JSONArray(commentarray); // convert string to json

            if(comments.length()<1)
            {
                nocomment.setVisibility(View.VISIBLE);
                recy_view.setVisibility(View.GONE);

            }



            if(comments.length()>=1)
            {
                norating.setVisibility(View.GONE);
                int totalrating=0;
                int nofrating=0;

                for(int counter=comments.length() - 1; counter >= 0;counter--){
                    JSONObject comment = comments.getJSONObject(counter);

                    String name,com,rating;

                    name = comment.getString("username");
                    com = comment.getString("comment");
                    rating = comment.getString("rating");

                    totalrating += Integer.valueOf(rating);
                    nofrating +=1;

                    Comment c = new Comment(com,name,rating);
                    comment_list.add(c);
                }


             /*   for(int i=0;i<comments.length();i++)
                {
                    JSONObject comment = comments.getJSONObject(i);

                    String name,com,rating;

                    name = comment.getString("username");
                    com = comment.getString("comment");
                    rating = comment.getString("rating");

                    totalrating += Integer.valueOf(rating);
                    nofrating +=1;

                    Comment c = new Comment(com,name,rating);
                    comment_list.add(c);
                }*/

                int avgr = totalrating/nofrating;
                avgrating.setText(String.valueOf(avgr));

            }



        }catch (Exception e){
            e.printStackTrace();
        }

        adapter1 = new CommentsAdapter(comment_list);
        Log.d("length",String.valueOf(comment_list.size()));

        recy_view = (RecyclerView)findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recy_view.setLayoutManager(layoutManager);
        recy_view.setItemAnimator(new DefaultItemAnimator());
        recy_view.setAdapter(adapter1);


        if(imgencoded.equalsIgnoreCase(""))
        {
            //img.setImageResource(R.drawable.noimg);
           // img.setBackgroundColor(Color.TRANSPARENT);
        }
        else{
            byte[] decodedString = Base64.decode(imgencoded, Base64.NO_WRAP);
            Bitmap decodedImg = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            img.setImageBitmap(decodedImg);
        }


        ratingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                latestrating = String.valueOf((int)rating);
            }
        });

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(),MapActivity.class);

                intent.putExtra("dlat",latitude);
                intent.putExtra("dlong",longtitude);

                if(lat==null && longt==null){
                    Toast.makeText(getApplicationContext(),"Please ensure that your gps is on",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    intent.putExtra("olat",lat);
                    intent.putExtra("olong",longt);

                    Log.d("abcd",latitude+longtitude);
                    Log.d("def",lat+longt);

                    startActivity(intent);
                }
            }
        });

        addcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(commen.getText().toString().equalsIgnoreCase(""))
                {
                    Toast.makeText(getBaseContext(),"Please rate and comment before you submit your review",Toast.LENGTH_SHORT);
                }
                else
                {

                    JSONObject data = new JSONObject();
                    try{
                        data.put("name", namestr);
                        data.put("address",addr);
                        data.put("lat",latitude);
                        data.put("long",longtitude);
                        data.put("desc",descstr);
                        data.put("image",imgencoded);
                        data.put("category",categorystr);


                        Log.d("aaaaaa",comments.toString() );

                        // comments //comments is json array
                        JSONObject comment = new JSONObject();
                        //comment.put("rating",spinner.getSelectedItem().toString());
                        comment.put("rating",String.valueOf(latestrating));
                        comment.put("comment",commen.getText().toString());
                        comment.put("username",username.getText().toString());


                        comments.put(comment);

                        data.put("comments",comments);

                       // Log.d("aaaaaa",spinner.getSelectedItem().toString() + "    " + commen.getText().toString() );
                        //Log.d("aaaaaa",comments.toString() );

                       TaskParams param = new TaskParams(data,id);
                       new postCom().execute(param);
                      //  postComment(data.toString(),id);

                       // Toast.makeText(getBaseContext(),"posted",Toast.LENGTH_SHORT);


                    }catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }

            }
        });

    }



    public static String postComment(String json, String id) {
        HttpURLConnection urlConnection;
        //String url;
        String data = json;
        String result = null;
        try {
            //Connect
            urlConnection = (HttpURLConnection) ((new URL("https://powerful-escarpment-79209.herokuapp.com/api/place/"+id).openConnection()));
            urlConnection.setDoOutput(true);
            // urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestMethod("PUT");
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

    private static class TaskParams{
        JSONObject json;
        String id;

        public TaskParams(JSONObject json, String id) {
            this.json = json;
            this.id = id;
        }
    }

    class postCom extends AsyncTask<TaskParams,Boolean,Boolean>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(TaskParams... params) {
            TaskParams paramobj = params[0];

            String result = postComment(paramobj.json.toString(),paramobj.id);
            //return true;
            try{
                JSONObject returned =  new JSONObject(result);
                //Log.d("return",returned.toString());

                returned.remove("_id");
                returned.remove("image"); // remove irrelevent data for comparison
                returned.remove("__v");

                JSONObject original = paramobj.json;
                original.remove("image"); // image encoding seems to change on put requests, but
                                            //decoded image stays the same. removed from comparison

                JSONAssert.assertEquals(returned,original,true); // check if same
                return true;

            }catch (JSONException e)
            {
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if(aBoolean)
            {
                Toast.makeText(getBaseContext(),"Successfully commented",Toast.LENGTH_SHORT).show();

                try{ // refresh data

                    JSONObject updated = getOnePlace(id);

                    Intent intent1 = new Intent();
                    intent1.setClass(getApplicationContext(),ShowPlaceActivity.class);

                    intent1.putExtra("name",updated.getString("name"));
                    intent1.putExtra("address",updated.getString("address"));
                    intent1.putExtra("desc",updated.getString("desc"));
                    intent1.putExtra("category",updated.getString("category"));
                    intent1.putExtra("image",updated.getString("image"));
                    intent1.putExtra("lat",updated.getString("lat"));
                    intent1.putExtra("long",updated.getString("long"));

                    intent1.putExtra("id",updated.getString("_id"));
                    intent1.putExtra("comments",updated.getString("comments"));
                    intent1.putExtra("status","ok"); // to indicate comment has occured


                    finish();
                    overridePendingTransition(0, 0);

                    intent1.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent1);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(getBaseContext(),"Something went wrong,please try again later",Toast.LENGTH_SHORT).show();
            }
        }
    }


    private JSONObject getOnePlace(String id) throws IOException {

        InputStream is = null;
        JSONObject obj = new JSONObject();


        String link = "https://powerful-escarpment-79209.herokuapp.com/api/place/"+id;
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
    public void onBackPressed() {

        if(bundle.getString("status")!=null){
            Log.d("OK","OK"); // it can reach here

            finish();
            super.onBackPressed();
        }
        else{
            finish1();
            super.onBackPressed();
        }



    }

    @Override
    public void finish() {
        Intent data = new Intent();
        setResult(RESULT_OK, data);
        super.finish();
    }

    public void finish1() {
        Intent data = new Intent();
        setResult(RESULT_CANCELED, data);
        super.finish();
    }

}
