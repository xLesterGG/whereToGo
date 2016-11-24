package com.example.lesgo.wheretogo;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment extends Fragment {
    List<Place> place_list = new ArrayList<>();
    int count = 0;


    public BlankFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_blank, container, false);

        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.rv_recycler_view);
        rv.setHasFixedSize(true);

        String type = getArguments().getString("id");

        try{
            JSONArray arr = new JSONArray(getArguments().getString("data"));
           // Log.d("legtrh is",String.valueOf(arr.length()));

            if(arr!= null && count==0)
            {
                for(int i=0; i< arr.length();i++){
                    JSONObject pointed = arr.getJSONObject(i);
                    JSONArray comments;

                    String name,address,longt,lat,desc,img,category,id;

                    category = pointed.getString("category");

                    if(type.equalsIgnoreCase("1") && category.equalsIgnoreCase("restaurant"))
                    {
                        name= pointed.getString("name");
                        address = pointed.getString("address");
                        longt = pointed.getString("long");
                        lat = pointed.getString("lat");
                        desc = pointed.getString("desc");
                        img = pointed.getString("image");


                        comments = pointed.getJSONArray("comments");
                        id = pointed.getString("_id");

                        byte[] decodedString = Base64.decode(img, Base64.NO_WRAP);
                        Bitmap decodedImg = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        Place place = new Place(name,address,longt,lat,desc,decodedImg,category,img,comments,id);
                        place_list.add(place);
                    }
                    else if(type.equalsIgnoreCase("2") && category.equalsIgnoreCase("accomodation"))
                    {
                        name= pointed.getString("name");
                        address = pointed.getString("address");
                        longt = pointed.getString("long");
                        lat = pointed.getString("lat");
                        desc = pointed.getString("desc");
                        img = pointed.getString("image");


                        comments = pointed.getJSONArray("comments");
                        id = pointed.getString("_id");

                        byte[] decodedString = Base64.decode(img, Base64.NO_WRAP);
                        Bitmap decodedImg = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        Place place = new Place(name,address,longt,lat,desc,decodedImg,category,img,comments,id);
                        place_list.add(place);
                    }
                    else if(type.equalsIgnoreCase("3") && category.equalsIgnoreCase("local attractions"))
                    {
                        name= pointed.getString("name");
                        address = pointed.getString("address");
                        longt = pointed.getString("long");
                        lat = pointed.getString("lat");
                        desc = pointed.getString("desc");
                        img = pointed.getString("image");


                        comments = pointed.getJSONArray("comments");
                        id = pointed.getString("_id");

                        byte[] decodedString = Base64.decode(img, Base64.NO_WRAP);
                        Bitmap decodedImg = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        Place place = new Place(name,address,longt,lat,desc,decodedImg,category,img,comments,id);
                        place_list.add(place);
                    }


                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }

        count+=1;
        CustomAdapter adapter = new CustomAdapter(place_list);
        adapter.setOnItemClickListener(new CustomAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(Place place) {

                Intent intent = new Intent();
                intent.setClass(getActivity().getApplicationContext(),ShowPlaceActivity.class);
                intent.putExtra("name",place.getName());
                intent.putExtra("address",place.getAddress());
                intent.putExtra("desc",place.getDesc());
                intent.putExtra("category",place.getCategory());
                intent.putExtra("image",place.getImgstring());
                intent.putExtra("lat",place.getLat());
                intent.putExtra("long",place.getLongi());

                //Log.d("hereabc",place.getLat()+place.getLongi());

                intent.putExtra("id",place.getId());
                intent.putExtra("comments",place.getComments().toString());
                startActivity(intent);

            }
        });

        rv.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        return rootView;
    }

}
