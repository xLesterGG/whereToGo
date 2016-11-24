package com.example.lesgo.wheretogo;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.RequestResult;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback{
    LatLng origin,destination;
    Bundle bundle;
    String olat, olong, dlat,dlong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        bundle = getIntent().getExtras();
        olat = bundle.getString("olat");
        olong = bundle.getString("olong");

        dlat = bundle.getString("dlat");
        dlong = bundle.getString("dlong");

        Log.d("ori", olat + "aaaa" + olong);
        origin = new LatLng(Double.parseDouble(olat),Double.parseDouble(olong));

        destination = new LatLng(Double.parseDouble(dlat),Double.parseDouble(dlong));



        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        String serverKey = "AIzaSyC16yqB3Yakfer_goQ7L7tZ_PHRTVUwqoA";
       /* final LatLng origin = new LatLng(37.7849569, -122.4068855);
        final LatLng destination = new LatLng(37.7814432, -122.4460177);*/
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .alternativeRoute(true)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        // Do something here
                        String status = direction.getStatus();
                        if(status.equals(RequestResult.OK)) {
                            // Do something
                            Log.d("okla","ok");

                            Route route = direction.getRouteList().get(0);
                            Leg leg = route.getLegList().get(0);

                            ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                            PolylineOptions polylineOptions = DirectionConverter.createPolyline(getApplicationContext(),directionPositionList,5, Color.RED);
                            googleMap.addPolyline(polylineOptions);

                            // googleMap.moveCamera(CameraUpdateFactory.newLatLng(origin));

                            MarkerOptions a = new MarkerOptions();
                            a.position(origin);
                            a.title("Current location");

                            MarkerOptions b = new MarkerOptions();
                            b.position(destination);
                            b.title("Destination");

                            ArrayList<MarkerOptions> markers = new ArrayList<>() ;
                            markers.add(a);
                            markers.add(b);

                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            for (MarkerOptions marker : markers) {
                                builder.include(marker.getPosition());
                            }
                            LatLngBounds bounds = builder.build();

                            int padding = 50; // offset from edges of the map in pixels
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                            googleMap.animateCamera(cu);

                            googleMap.addMarker(a);
                            googleMap.addMarker(b);

                           /* CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(origin,13);
                            googleMap.animateCamera(yourLocation);*/

                        }
                        else if (status.equals(RequestResult.NOT_FOUND)){
                            Log.d("somethingelse","ok");
                        }else if (status.equals(RequestResult.REQUEST_DENIED)){
                            Log.d("somethingelse1","ok");
                        }else if (status.equals(RequestResult.UNKNOWN_ERROR)){
                            Log.d("somethingelse2","ok");
                        }else if (status.equals(RequestResult.NOT_FOUND)){
                            Log.d("somethingelse3","ok");
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        // Do something here
                        Log.d("error","eror");
                    }
                });

    }



}
