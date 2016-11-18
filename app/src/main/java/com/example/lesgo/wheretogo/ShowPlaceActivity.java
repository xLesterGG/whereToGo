package com.example.lesgo.wheretogo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowPlaceActivity extends AppCompatActivity {

    TextView name,category,address,desc;
    ImageView img;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_place);

        name = (TextView)findViewById(R.id.name);
        category = (TextView)findViewById(R.id.category);
        address =(TextView)findViewById(R.id.address);
        desc = (TextView)findViewById(R.id.desc);
        img  = (ImageView)findViewById(R.id.image);

        bundle = getIntent().getExtras();
        String namestr = bundle.getString("name");
        String categorystr = bundle.getString("category");
        String addr = bundle.getString("address");
        String descstr = bundle.getString("desc");
        String imgencoded = bundle.getString("image");
        String latitude = bundle.getString("lat");
        String longtitude = bundle.getString("long");


        byte[] decodedString = Base64.decode(imgencoded, Base64.NO_WRAP);
        Bitmap decodedImg = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        name.setText(namestr);
        category.setText(categorystr);
        address.setText(addr);
        desc.setText(descstr);
        img.setImageBitmap(decodedImg);





    }
}
