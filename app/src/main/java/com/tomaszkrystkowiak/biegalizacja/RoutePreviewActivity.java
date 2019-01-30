package com.tomaszkrystkowiak.biegalizacja;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class RoutePreviewActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "RoutePreviewActivity";
    private GoogleMap mMap;
    private Polyline route;
    private ArrayList<LatLng> routePointsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_preview);
        routePointsList = getIntent().getParcelableArrayListExtra("route");
        Log.i(TAG, "onCreate: ");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.BLACK);
        polylineOptions.width(20);
        route = mMap.addPolyline(polylineOptions);
        route.setPoints(routePointsList);
        Log.i(TAG, "onMapReady: isEmpty"+ routePointsList.isEmpty());
        LatLng start = routePointsList.get(0);
        LatLng finish = routePointsList.get(routePointsList.size()-1);
        mMap.addMarker(new MarkerOptions().position(start).title("Start"));
        mMap.addMarker(new MarkerOptions().position(finish).title("Finish"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(start.latitude, start.longitude), 17));

    }
}
