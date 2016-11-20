package com.example.lesgo.wheretogo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ShowPlaceActivity extends AppCompatActivity {

    TextView name,category,address,desc;
    ImageView img;
    Bundle bundle;
    EditText commen,username;
    JSONArray comments;
    Button addcomment;
    String namestr,categorystr,addr,descstr,imgencoded,latitude,longtitude,id,commentarray;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_place);

        name = (TextView)findViewById(R.id.name);
        category = (TextView)findViewById(R.id.category);
        address =(TextView)findViewById(R.id.address);
        desc = (TextView)findViewById(R.id.desc);
        img  = (ImageView)findViewById(R.id.image);
        commen = (EditText)findViewById(R.id.comment);
        addcomment = (Button)findViewById(R.id.submitcomment);
        username = (EditText)findViewById(R.id.username);


        bundle = getIntent().getExtras();
        namestr = bundle.getString("name");
        categorystr = bundle.getString("category");
        addr = bundle.getString("address");
        descstr = bundle.getString("desc");
        imgencoded = bundle.getString("image");
        latitude = bundle.getString("lat");
        longtitude = bundle.getString("long");

        id = bundle.getString("id");

        commentarray = bundle.getString("comments");


        spinner = (Spinner)findViewById(R.id.spin);
        String[] items = new String[]{"1","2","3","4","5"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);


        try{
            comments = new JSONArray(commentarray);
        }catch (Exception e){
            e.printStackTrace();
        }


        byte[] decodedString = Base64.decode(imgencoded, Base64.NO_WRAP);
        Bitmap decodedImg = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        name.setText(namestr);
        category.setText(categorystr);
        address.setText(addr);
        desc.setText(descstr);
        img.setImageBitmap(decodedImg);


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
                        comment.put("rating",spinner.getSelectedItem().toString());
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
            }
            else{
                Toast.makeText(getBaseContext(),"Something went wrong,please try again later",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
