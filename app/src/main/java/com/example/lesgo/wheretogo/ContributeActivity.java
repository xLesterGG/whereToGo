package com.example.lesgo.wheretogo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.uber.sdk.android.rides.RideRequestButton;

import org.apache.commons.io.output.ByteArrayOutputStream;
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



public class ContributeActivity extends AppCompatActivity implements AsyncResponse1{

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
    int errorcount=0;
    AlertDialog.Builder builder1;

   // ASCallApi astask= new ASCallApi();
    PostData asyncTask =new PostData();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contribute);

       // astask.delegate = this;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);



        submit = (Button)findViewById(R.id.submit);
        eName = (EditText)findViewById(R.id.name);
        eAddress= (EditText)findViewById(R.id.address);
       // eAddress.setText("1327e, Lorong Bayor Bukit 16, Tabuan Jaya, 93350 Kuching, Sarawak, Malaysia");
        eLat= (EditText)findViewById(R.id.latitude);
        eLong= (EditText)findViewById(R.id.longitude);
        eDesc= (EditText)findViewById(R.id.description);
       /* picture = (Button)findViewById(R.id.picture) ;*/
        img = (ImageView)findViewById(R.id.img1);
        gps = (TextView)findViewById(R.id.gpsabc);
        spinner = (Spinner)findViewById(R.id.planets_spinner);

        builder1 = new AlertDialog.Builder(this);

        String[] items = new String[]{"Restaurant","Accomodation", "Local attractions"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        Location a = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
      /*  String lt = String.valueOf(a.getLatitude());
        String lg = String.valueOf(a.getLongitude());*/

        if(a!=null)
        {

            lat = String.valueOf(a.getLatitude());
            longt = String.valueOf(a.getLongitude());
           // Toast.makeText(getApplicationContext(),lat+longt,Toast.LENGTH_SHORT).show();
        }

        //Toast.makeText(getApplicationContext(), lt + "        " + lg , Toast.LENGTH_SHORT).show();

        // Define a listener that responds to location updates
        llistener = new LocationListener() {
            public void onLocationChanged(Location loc) {
                // Called when a new location is found by the network location provider.
                lat = String.valueOf(loc.getLatitude());
                longt = String.valueOf(loc.getLongitude());
              /*  Toast.makeText(
                        getBaseContext(),
                        "Location changed: Lat: " + loc.getLatitude() + " Lng: "
                                + loc.getLongitude(), Toast.LENGTH_SHORT).show();*/
                gps.setText("GPS is Activated");
                eLat.setText(lat);
                eLong.setText(longt);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {
                Toast.makeText(getApplicationContext(),"GPS has been enabled",Toast.LENGTH_SHORT).show();
            }
            public void onProviderDisabled(String provider) {
                Toast.makeText(getApplicationContext(),"GPS has been disabled, please enable it",Toast.LENGTH_SHORT).show();

            }
        };

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });

          submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(eName.getText().toString().equalsIgnoreCase("") || eAddress.getText().toString().equalsIgnoreCase("")
                        || eDesc.getText().toString().equalsIgnoreCase(""))
                {
                    Toast.makeText(getBaseContext(),"Please fill in all the details before submitting",Toast.LENGTH_SHORT).show();
                }
                else{

                    if(lat==null && longt==null)
                    {
                        Toast.makeText(getApplicationContext(),"Your GPS might not be on, please ensure it is on",Toast.LENGTH_SHORT).show();
                    }

                    if(lat!=null && longt!=null)
                    {
                       // astask.delegate = this;

                        ASCallApi ast = new ASCallApi();
                        ast.delegate = ContributeActivity.this;
                        ast.execute(new TaskParams(lat,longt));

                       /* astask = new ASCallApi();
                        astask.execute(new TaskParams(lat,longt));*/



                    }
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

        if(i >= rounded )
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
            urlConnection = (HttpURLConnection) ((new URL("http://powerful-escarpment-79209.herokuapp.com/api/place").openConnection()));
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
        Log.d("result",result.toString());
        return result;
    }

    private JSONObject callApi(String lat,String longt) throws IOException {
        InputStream is = null;
        JSONObject obj = new JSONObject();

        Log.d("wewew", "lat" + lat + "long" + longt);

        String link = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+ lat +","+ longt + "&key=AIzaSyCGeq_hDf3tFAb71Ho_3meOmiQQqqJ6JdE";

      //  String link = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+ "110.377240" +","+ "1.532536" + "&key=AIzaSyB-32aoxlx7d_aoJMbJvG1PY99Tm4F-hgM";
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

    @Override
    public void processFinish(JSONObject obj) {

        try{
            final JSONObject data = new JSONObject();

            data.put("name", eName.getText().toString());
            data.put("address",eAddress.getText().toString());
            data.put("lat",lat);
            data.put("long",longt);
            data.put("desc",eDesc.getText().toString());
            data.put("image",selectedImg.replace("=",""));
            data.put("category",spinner.getSelectedItem().toString());


            //JSONArray result = obj.getJSONArray("results");
            //Log.d("result",result.toString());

            if(obj!=null)
            {
                JSONArray result = obj.getJSONArray("results");
                Log.d("result",result.toString());

                JSONObject first = result.getJSONObject(0);
                String address = first.getString("formatted_address");
                String sample = address.replace(",", "");
                String[] elements = sample.split(" ");

                String input = eAddress.getText().toString();
                input = input.replace(",", "");
                String[] inputs = input.split(" ");

                Boolean add = checkAddress(elements,inputs,elements.length);

                //  Boolean add = true;
                if(add)
                {

                    if(selectedImg.equalsIgnoreCase(""))
                    {
                        builder1.setTitle("Confirm submission");
                        builder1.setMessage("Are you sure you want to continue without adding an image?");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int ida) {
                                        Toast.makeText(getBaseContext(),"Address is correct",Toast.LENGTH_SHORT).show();
                                        //new PostData().execute(data);
                                        //execute the async task
                                        asyncTask.execute(data);
                                        errorcount =0;
                                        dialog.cancel();
                                    }
                                });

                        builder1.setNegativeButton(
                                "No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();

                    }
                    else{

                        builder1.setTitle("Confirm submission");
                        builder1.setMessage("Are you sure?");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int ida) {
                                        Toast.makeText(getBaseContext(),"Address is correct",Toast.LENGTH_SHORT).show();
                                        // new PostData().execute(data);
                                        asyncTask.execute(data);
                                        errorcount =0;
                                    }
                                });

                        builder1.setNegativeButton(
                                "No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();


                    }
                }
                else{
                    errorcount+=1;
                    if(errorcount>5)
                    {
                        Toast.makeText(getBaseContext(),"You seem to not know your the accurate address of your location. " +
                                "Please get the accurate address before trying again",Toast.LENGTH_SHORT).show();

                    }
                    else{
                        Toast.makeText(getBaseContext(),"Either you have enterred an incorrect address or " +
                                "you are not at the correct location",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else{
                Toast.makeText(getApplicationContext(),"Cannot check address, please try again later",Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        Log.d("done","done");
    }

    class PostData extends AsyncTask<JSONObject,JSONObject,JSONObject>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(JSONObject... params) {
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
                    Log.d("returning not null","ASDsad");
                    return resultobj;
                }


            }catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if(jsonObject!=null)
            {
                Log.d("length not null",String.valueOf(jsonObject.length()));
                Toast.makeText(getApplicationContext(),"Successfully contributed",Toast.LENGTH_SHORT).show();

                Intent intent1 = new Intent();
                intent1.setClass(getApplicationContext(),ShowPlaceActivity.class);

                try{
                    intent1.putExtra("name",jsonObject.getString("name"));
                    intent1.putExtra("address",jsonObject.getString("address"));
                    intent1.putExtra("desc",jsonObject.getString("desc"));
                    intent1.putExtra("category",jsonObject.getString("category"));
                    intent1.putExtra("image",jsonObject.getString("image"));
                    intent1.putExtra("lat",jsonObject.getString("lat"));
                    intent1.putExtra("long",jsonObject.getString("long"));

                    intent1.putExtra("id",jsonObject.getString("_id"));
                    intent1.putExtra("comments",jsonObject.getString("comments"));

                    finish();

                    startActivity(intent1);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Something went wrong,please try again later",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static class TaskParams{
        String lat;
        String longt;


        public TaskParams(String lat, String longt) {
            this.lat = lat;
            this.longt = longt;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLongt() {
            return longt;
        }

        public void setLongt(String longt) {
            this.longt = longt;
        }
    }


    class ASCallApi extends AsyncTask<TaskParams,JSONObject,JSONObject>
    {
        public AsyncResponse1 delegate = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected JSONObject doInBackground(TaskParams... params) {

            TaskParams param = params[0];
            try {
                JSONObject ob = callApi(param.getLat(), param.getLongt());

                if (ob != null)
                {
                    return ob;
                }


            }catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            delegate.processFinish(jsonObject);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK) { // picking image from gallery

            try{
                InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
                selectedImage = BitmapFactory.decodeStream(inputStream);
                img.setImageBitmap(selectedImage);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 20, baos);

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
