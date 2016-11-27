package com.example.lesgo.wheretogo;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
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

    JSONArray alldata;
    getAll task = new getAll();

    public static final int SPINNER = 2;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        task.delegate = this;
        task.execute();
    }


    @Override
    protected Dialog onCreateDialog(int id) {


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Getting available places...");

        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        return progressDialog;
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
        Log.d("length",String.valueOf(alldata.length()));


        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        PagerAdapter pagerAdapter =
                new PagerAdapter(getSupportFragmentManager(), SearchActivity.this,alldata);
        viewPager.setAdapter(pagerAdapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        // Iterate over all tabs and set the custom view
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(pagerAdapter.getTabView(i));
        }

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


    }

    class getAll extends AsyncTask<Void,JSONArray,JSONArray>
    {
        public AsyncResponse delegate = null;

        JSONArray arr;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(SPINNER);
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
            dismissDialog(SPINNER);
            delegate.processFinish(jsonArray);
        }


    }

    class PagerAdapter extends FragmentPagerAdapter {

        String tabTitles[] = new String[] { "Restaurant", "Accomodation", "Local attractions"};
        Context context;
        JSONArray ar;

        public PagerAdapter(FragmentManager fm, Context context,JSONArray arr) {
            super(fm);
            this.context = context;
            this.ar = arr;
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    Bundle arg = new Bundle();
                    arg.putString("data",ar.toString());
                    arg.putString("id","1");
                    BlankFragment a = new BlankFragment();
                    a.setArguments(arg);
                    return a;
                case 1:
                    Bundle arg1 = new Bundle();
                    arg1.putString("data",ar.toString());
                    arg1.putString("id","2");
                    BlankFragment b = new BlankFragment();
                    b.setArguments(arg1);
                    return b;
                case 2:
                    Bundle arg2 = new Bundle();
                    arg2.putString("data",ar.toString());
                    arg2.putString("id","3");
                    BlankFragment c = new BlankFragment();
                    c.setArguments(arg2);
                    return c;
            }

            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitles[position];
        }

        public View getTabView(int position) {
            View tab = LayoutInflater.from(SearchActivity.this).inflate(R.layout.custom_tab, null);
            TextView tv = (TextView) tab.findViewById(R.id.custom_text);
            tv.setText(tabTitles[position]);
            return tab;
        }

    }

}
